package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.*;
import com.example.basic.domain.DidaResponse;
import com.example.basic.domain.dida.*;
import com.example.basic.entity.*;
import com.example.basic.utils.HttpUtils;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author han
 * {@code @date} 2025/1/26
 */
@Slf4j
@Service
public class DidaService {

    @Resource
    private DidaCountryDao didaCountryDao;

    @Resource
    private DidaHotelIdDao didaHotelIdDao;

    @Resource
    private DidaHotelInfoDao didaHotelInfoDao;
    @Resource
    private DidaHotelPolicyDao didaHotelPolicyDao;
    @Resource
    private DidaHotelImageDao didaHotelImageDao;
    @Resource
    private DidaHotelFacilitiesDao didaHotelFacilitiesDao;
    @Resource
    private DidaHotelRoomDao didaHotelRoomDao;
    @Resource
    private DidaHotelRoomImagesDao didaHotelRoomImagesDao;

    @Resource
    private HttpUtils httpUtils;

    private static final Executor executor =
            new ThreadPoolExecutor(
                    20,
                    50,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(1000));

    private static final Executor executor2 =
            new ThreadPoolExecutor(
                    20,
                    40,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(50000));

    public void pullCountry() {
        //firstPull();
        String json = httpUtils.pullDidaCountryCn();
        DidaResponse didaResponse = JSON.parseObject(json, DidaResponse.class);
        String data = didaResponse.getData();
        JSONArray jsonArray = JSON.parseArray(data);
        List<DidaCountry> didaCountryList = Lists.newArrayListWithExpectedSize(jsonArray.size());
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            DidaCountry didaCountry = new DidaCountry();
            didaCountry.setCode(jsonObject.get("code").toString());
            didaCountry.setName(jsonObject.get("name").toString());
            didaCountryList.add(didaCountry);
        }
        for (DidaCountry didaCountry : didaCountryList) {
            didaCountryDao.updateName(didaCountry);
        }
    }

    private void firstPull() {
        String json = httpUtils.pullDidaCountryEn();
        DidaResponse didaResponse = JSON.parseObject(json, DidaResponse.class);
        String data = didaResponse.getData();
        JSONArray jsonArray = JSON.parseArray(data);
        List<DidaCountry> didaCountryList = Lists.newArrayListWithExpectedSize(jsonArray.size());
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            DidaCountry didaCountry = new DidaCountry();
            didaCountry.setCode(jsonObject.get("code").toString());
            didaCountry.setNameEn(jsonObject.get("name").toString());
            didaCountryList.add(didaCountry);
        }
        didaCountryDao.saveBatch(didaCountryList);
    }

    public void analyzeHotelInfo() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\dida\\HotelSummary.csv";
        List<String> lines = Files.readLines(new File(fileName), Charset.defaultCharset());
        List<DidaHotelId> insertList = Lists.newArrayListWithCapacity(lines.size());
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            DidaHotelId didaHotelId = new DidaHotelId();
            didaHotelId.setHotelId(Integer.valueOf(line.split("\\|")[0]));
            insertList.add(didaHotelId);
        }
        List<List<DidaHotelId>> partition = Lists.partition(insertList, 5000);
        partition.forEach(e->didaHotelIdDao.saveBatch(e));
    }

    public void pullHotelInfo() {
        List<Integer> hotelIds = didaHotelIdDao.selectAll();
        Set<Long> ids = didaHotelInfoDao.selectHotelIds();
        hotelIds = hotelIds.stream().filter(e->!ids.contains(Long.valueOf(e))).toList();
        int apiLimit = 10;
        int onceQueryHotelIdCount = 50;
        int onceBatchHotelIdSize = apiLimit * onceQueryHotelIdCount;
        int total = hotelIds.size();
        int totalProcess = total / onceBatchHotelIdSize;
        int process = 1;
        for (int i = 0; i < total; ) {
            // 一次查询id列表
            List<Integer> onceBatchIdList = hotelIds.subList(i, Math.min(total, i + onceBatchHotelIdSize));
            StopWatch watch = new StopWatch();
            watch.start("查询一批酒店");
            List<DidaHotelInfoResult> hotelInfoList = queryHotelInfo(onceBatchIdList, apiLimit, onceQueryHotelIdCount, true);
            watch.stop();
            watch.start("转换并入库");
            transferAndSaveEn(hotelInfoList);
            watch.stop();
            if (CollectionUtils.isEmpty(hotelInfoList)) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignore) {
                }
                // 没返回猎豹，默认Too Many Requests,不进行步长累计
                continue;
            }
            i += onceBatchHotelIdSize;
            process++;
            log.info("当前批次{}/{}, 耗时{},分别耗时为:{}", process, totalProcess, watch.getTotalTimeSeconds(), watch.prettyPrint());
        }
    }

    private void transferAndSaveEn(List<DidaHotelInfoResult> hotelInfoList) {
        int size = hotelInfoList.size();
        List<DidaHotelInfo> infos = Lists.newArrayListWithExpectedSize(size);
        List<DidaHotelPolicy> policies = Lists.newArrayListWithExpectedSize(size);
        List<DidaHotelImage> images = Lists.newArrayListWithExpectedSize(size * 50);
        List<DidaHotelFacilities> facilities = Lists.newArrayListWithExpectedSize(size * 80);
        List<DidaHotelRoom> rooms = Lists.newArrayListWithExpectedSize(size * 20);
        List<DidaHotelRoomImages> roomImages = Lists.newArrayListWithExpectedSize(size * 80);
        for (DidaHotelInfoResult hotelInfo : hotelInfoList) {
            long hotelId = hotelInfo.getId();
            DidaHotelInfo info = new DidaHotelInfo();
            info.setHotelId(hotelId);
            info.setNameEn(hotelInfo.getName());
            info.setTelephone(hotelInfo.getTelephone());
            info.setStarRating(BigDecimal.valueOf(hotelInfo.getStarRating()));
            info.setZipCode(hotelInfo.getZipCode());
            info.setAirportCode(hotelInfo.getAirportCode());
            info.setDescriptionEn(hotelInfo.getDescription());
            List<String> giataCodes = hotelInfo.getGiataCodes();
            if (!CollectionUtils.isEmpty(giataCodes)) {
                info.setGiataCodes(JSON.toJSONString(giataCodes));
            }
            Location location = hotelInfo.getLocation();
            info.setAddress(location.getAddress());
            info.setStateCode(location.getStateCode());
            Country country = location.getCountry();
            info.setCountryCode(country.getCode());
            info.setCountryNameEn(country.getName());
            Destination destination = location.getDestination();
            info.setDestinationCode(StringUtils.hasLength(destination.getCode()) ? Long.valueOf(destination.getCode()) : null);
            info.setDestinationNameEn(destination.getName());
            Coordinate coordinate = location.getCoordinate();
            if (coordinate != null) {
                info.setLatitude(BigDecimal.valueOf(coordinate.getLatitude()));
                info.setLongitude(BigDecimal.valueOf(coordinate.getLongitude()));
            }
            Category category = hotelInfo.getCategory();
            if (category != null) {
                info.setCategoryCode(StringUtils.hasLength(category.getCode()) ? Integer.valueOf(category.getCode()) : null);
                info.setCategoryEn(category.getName());
            }
            infos.add(info);
            Policy policy = hotelInfo.getPolicy();
            if (policy != null) {
                DidaHotelPolicy didaHotelPolicy = new DidaHotelPolicy();
                didaHotelPolicy.setHotelId(hotelId);
                didaHotelPolicy.setDescriptionEn(policy.getDescription());
                didaHotelPolicy.setCheckinFrom(policy.getCheckinFrom());
                didaHotelPolicy.setCheckoutTo(policy.getCheckoutTo());
                didaHotelPolicy.setImportantNotice(policy.getImportantNotice());
                List<ExtraInfo> extraInfoList = policy.getExtraInfoList();
                if (!CollectionUtils.isEmpty(extraInfoList)) {
                    didaHotelPolicy.setExtraInfoListEn(JSON.toJSONString(extraInfoList));
                }
                policies.add(didaHotelPolicy);
            }
            List<Images> hotelInfoImages = hotelInfo.getImages();
            if (!CollectionUtils.isEmpty(hotelInfoImages)) {
                for (Images hotelInfoImage : hotelInfoImages) {
                    DidaHotelImage image = new DidaHotelImage();
                    image.setHotelId(hotelId);
                    image.setHeroImage(hotelInfoImage.getIsDefault());
                    image.setUrl(hotelInfoImage.getUrl());
                    image.setCaptionEn(hotelInfoImage.getCaption());
                    images.add(image);
                }
            }
            List<Facility> hotelInfoFacilities = hotelInfo.getFacilities();
            if (!CollectionUtils.isEmpty(hotelInfoFacilities)) {
                for (Facility hotelInfoFacility : hotelInfoFacilities) {
                    DidaHotelFacilities facility = new DidaHotelFacilities();
                    facility.setHotelId(hotelId);
                    facility.setType(hotelInfoFacility.getType());
                    facility.setDescriptionEn(hotelInfoFacility.getDescription());
                    facility.setValue(hotelInfoFacility.getValue());
                    facilities.add(facility);
                }
            }
            List<Room> hotelInfoRooms = hotelInfo.getRooms();
            if (!CollectionUtils.isEmpty(hotelInfoRooms)) {
                for (Room hotelInfoRoom : hotelInfoRooms) {
                    DidaHotelRoom room = new DidaHotelRoom();
                    room.setHotelId(hotelId);
                    room.setRoomId(hotelInfoRoom.getId());
                    room.setNameEn(hotelInfoRoom.getName());
                    room.setSize(hotelInfoRoom.getSize());
                    room.setFloor(hotelInfoRoom.getFloor());
                    room.setHasWifi(hotelInfoRoom.getHasWifi());
                    room.setHasWindow(hotelInfoRoom.getHasWindow());
                    room.setMaxOccupancy(hotelInfoRoom.getMaxOccupancy());
                    rooms.add(room);

                    List<Images> infoRoomImages = hotelInfoRoom.getImages();
                    if (!CollectionUtils.isEmpty(infoRoomImages)) {
                        for (Images infoRoomImage : infoRoomImages) {
                            DidaHotelRoomImages didaHotelRoomImages = new DidaHotelRoomImages();
                            didaHotelRoomImages.setHotelId(hotelId);
                            didaHotelRoomImages.setRoomId(hotelInfoRoom.getId());
                            didaHotelRoomImages.setUrl(infoRoomImage.getUrl());
                            didaHotelRoomImages.setHeroImage(infoRoomImage.getIsDefault());
                            roomImages.add(didaHotelRoomImages);
                        }
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(infos)) {
            didaHotelInfoDao.saveBatch(infos);
        }
        if (!CollectionUtils.isEmpty(policies)) {
            didaHotelPolicyDao.saveBatch(policies);
        }
        if (!CollectionUtils.isEmpty(images)) {
            didaHotelImageDao.saveBatch(images);
        }
        if (!CollectionUtils.isEmpty(facilities)) {
            didaHotelFacilitiesDao.saveBatch(facilities);
        }
        if (!CollectionUtils.isEmpty(rooms)) {
            didaHotelRoomDao.saveBatch(rooms);
        }
        if (!CollectionUtils.isEmpty(roomImages)) {
            didaHotelRoomImagesDao.saveBatch(roomImages);
        }
    }

    private List<DidaHotelInfoResult> queryHotelInfo(List<Integer> onceBatchIdList, int apiLimit, int onceQueryHotelIdCount, boolean english) {
        List<DidaHotelInfoResult> hotelInfoList = Lists.newArrayListWithExpectedSize(apiLimit * onceQueryHotelIdCount);
        List<CompletableFuture<List<DidaHotelInfoResult>>> futures = Lists.newArrayListWithExpectedSize(apiLimit);
        int total = onceBatchIdList.size();
        for (int j = 0; j < apiLimit; j++) {
            int oneStart = j * onceQueryHotelIdCount;
            int oneEnd = Math.min(total, (j + 1) * onceQueryHotelIdCount);
            if (oneStart > total) break;
            List<Integer> onceQueryHotelIdList = onceBatchIdList.subList(oneStart, oneEnd);
            if (CollectionUtils.isEmpty(onceQueryHotelIdList)) break;
            futures.add(CompletableFuture.supplyAsync(() -> {
                if (english) {
                    String infoJson = httpUtils.pullDidaHotelInfoEn(onceQueryHotelIdList);
                    boolean hasInfo = StringUtils.hasLength(infoJson);
                    if (!hasInfo) return null;
                    return handlerInfo(infoJson);
                }
                String infoJson = httpUtils.pullDidaHotelInfoCn(onceQueryHotelIdList);
                boolean hasInfo = StringUtils.hasLength(infoJson);
                if (!hasInfo) return null;
                return handlerInfo(infoJson);
            }, executor));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        for (Future<List<DidaHotelInfoResult>> future : futures) {
            try {
                List<DidaHotelInfoResult> results = future.get(5, TimeUnit.SECONDS);
                if (CollectionUtils.isEmpty(results)) continue;
                hotelInfoList.addAll(results);
            } catch (Exception ignore) {
            }
        }
        return hotelInfoList;
    }

    private List<DidaHotelInfoResult> handlerInfo(String infoJson) {
        HotelResponse response = JSON.parseObject(infoJson, HotelResponse.class);
        return response.getData();
    }

    public void finishHotelInfo() {
        List<Integer> hotelIds = didaHotelIdDao.selectAll();
        List<DidaHotelRoom> rooms = didaHotelRoomDao.selectIds();
        int apiLimit = 10;
        int onceQueryHotelIdCount = 50;
        int onceBatchHotelIdSize = apiLimit * onceQueryHotelIdCount;
        int total = hotelIds.size();
        int totalProcess = total / onceBatchHotelIdSize;
        int process = 1;
        for (int i = 0; i < total; ) {
            // 一次查询id列表
            List<Integer> onceBatchIdList = hotelIds.subList(i, Math.min(total, i + onceBatchHotelIdSize));
            StopWatch watch = new StopWatch();
            watch.start("查询一批酒店");
            List<DidaHotelInfoResult> hotelInfoList = queryHotelInfo(onceBatchIdList, apiLimit, onceQueryHotelIdCount, false);
            watch.stop();
            watch.start("转换并入库");
            transferAndSaveCn(hotelInfoList, rooms);
            watch.stop();
            i += onceBatchHotelIdSize;
            process++;
            log.info("当前批次{}/{}, 耗时{},分别耗时为:{}", process, totalProcess, watch.getTotalTimeSeconds(), watch.prettyPrint());
        }
    }

    private void transferAndSaveCn(List<DidaHotelInfoResult> hotelInfoList, List<DidaHotelRoom> rooms) {
        Map<Long, List<DidaHotelRoom>> hotelIdListMap = rooms.stream().collect(Collectors.groupingBy(DidaHotelRoom::getHotelId));
        int size = hotelInfoList.size();
        List<DidaHotelInfo> infos = Lists.newArrayListWithExpectedSize(size);
        List<DidaHotelPolicy> policies = Lists.newArrayListWithExpectedSize(size);
        List<DidaHotelRoom> roomList = Lists.newArrayListWithExpectedSize(size * 20);
        for (DidaHotelInfoResult hotelInfo : hotelInfoList) {
            long hotelId = hotelInfo.getId();
            DidaHotelInfo info = new DidaHotelInfo();
            info.setHotelId(hotelId);
            info.setName(hotelInfo.getName());
            info.setDescription(hotelInfo.getDescription());
            Location location = hotelInfo.getLocation();
            Country country = location.getCountry();
            info.setCountryName(country.getName());
            Destination destination = location.getDestination();
            info.setDestinationNameEn(destination.getName());
            Category category = hotelInfo.getCategory();
            if (category != null) {
                info.setCategory(category.getName());
            }
            infos.add(info);
            Policy policy = hotelInfo.getPolicy();
            if (policy != null) {
                DidaHotelPolicy didaHotelPolicy = new DidaHotelPolicy();
                didaHotelPolicy.setHotelId(hotelId);
                didaHotelPolicy.setDescription(policy.getDescription());
                List<ExtraInfo> extraInfoList = policy.getExtraInfoList();
                if (!CollectionUtils.isEmpty(extraInfoList)) {
                    didaHotelPolicy.setExtraInfoList(JSON.toJSONString(extraInfoList));
                }
                policies.add(didaHotelPolicy);
            }
            List<Room> hotelInfoRooms = hotelInfo.getRooms();
            if (!CollectionUtils.isEmpty(hotelInfoRooms)) {
                List<DidaHotelRoom> didaHotelRooms = hotelIdListMap.get(hotelId);
                if (!CollectionUtils.isEmpty(didaHotelRooms)) {
                    Map<Integer, Long> roomIdIdMap = didaHotelRooms.stream().collect(Collectors.toMap(DidaHotelRoom::getRoomId, DidaHotelRoom::getId));
                    for (Room hotelInfoRoom : hotelInfoRooms) {
                        int roomId = hotelInfoRoom.getId();
                        Long id = roomIdIdMap.get(roomId);
                        if (id != null) {
                            DidaHotelRoom room = new DidaHotelRoom();
                            room.setName(hotelInfoRoom.getName());
                            room.setId(id);
                            roomList.add(room);
                        }
                    }
                }
            }
        }
        updateInfos(infos);
        updatePolicies(policies);
        updateRooms(roomList);
    }

    private void updateInfos(List<DidaHotelInfo> infos) {
        if (!CollectionUtils.isEmpty(infos)) {
            for (DidaHotelInfo info : infos) {
                CompletableFuture.runAsync(() -> didaHotelInfoDao.update(info), executor2);
            }
        }
    }

    private void updatePolicies(List<DidaHotelPolicy> policies) {
        if (!CollectionUtils.isEmpty(policies)) {
            for (DidaHotelPolicy policy : policies) {
                CompletableFuture.runAsync(() -> didaHotelPolicyDao.update(policy), executor2);
            }
        }
    }

    private void updateRooms(List<DidaHotelRoom> rooms) {
        if (!CollectionUtils.isEmpty(rooms)) {
            for (DidaHotelRoom room : rooms) {
                CompletableFuture.runAsync(() -> didaHotelRoomDao.update(room), executor2);
            }
        }
    }
}
