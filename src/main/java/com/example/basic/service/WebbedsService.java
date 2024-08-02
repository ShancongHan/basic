package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.basic.dao.*;
import com.example.basic.domain.CalculateResult;
import com.example.basic.domain.WebbedsDaolvMappingBean;
import com.example.basic.domain.WebbedsImportBean;
import com.example.basic.entity.*;
import com.example.basic.helper.MappingScoreHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author han
 * date 2024/5/28
 */
@Service
public class WebbedsService {

    @Resource
    private WebbedsHotelDataDao webbedsHotelDataDao;

    @Resource
    private JdJdbDaolvDao jdJdbDaolvDao;

    @Resource
    private WebbedsDaolvMappingDao webbedsDaolvMappingDao;

    @Resource
    private WebbedsDaolvMatchDao webbedsDaolvMatchDao;

    @Resource
    private JdJdbGjDao jdJdbGjDao;

    @Resource
    private WebbedsDaolvMatchLabCountDao webbedsDaolvMatchLabCountDao;

    @Resource
    private WebbedsDaolvMatchLabDao webbedsDaolvMatchLabDao;

    @Resource
    private WebbedsNotMatchDao webbedsNotMatchDao;

    @Resource
    private ZhJdJdbGjMappingDao zhJdJdbGjMappingDao;

    private static final Integer CORE_POOL_SIZE = 8;
    private static final Integer MAXIMUM_POOL_SIZE = 16;

    private static final Executor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10000));

    @Transactional
    public void importData() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\webbeds\\webbeds 全量.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<WebbedsImportBean> webbedsHotelDataList = Lists.newArrayListWithCapacity(310000);
        EasyExcel.read(inputStream, WebbedsImportBean.class, new PageReadListener<WebbedsImportBean>(webbedsHotelDataList::addAll, 1000)).headRowNumber(1).sheet().doRead();
        saveBatch(webbedsHotelDataList);
        //System.out.println(webbedsHotelDataList.size());
    }

    private void saveBatch(List<WebbedsImportBean> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<WebbedsImportBean> list = insertList.subList(start, j);
                webbedsHotelDataDao.saveBatch(list);
                start = j;
            }
        }
        List<WebbedsImportBean> list = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            webbedsHotelDataDao.saveBatch(list);
        }

    }

    @Transactional
    public void importMapping() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\webbeds\\MappedInventory-DIDA.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<WebbedsDaolvMapping> mappingList = Lists.newArrayListWithCapacity(1000);
        List<WebbedsDaolvMappingBean> errorDataList = Lists.newArrayListWithCapacity(1000);
        EasyExcel.read(inputStream, WebbedsDaolvMappingBean.class, new PageReadListener<WebbedsDaolvMappingBean>(dataList -> {
            for (WebbedsDaolvMappingBean bean : dataList) {
                if ("#N/A".equals(bean.getDaolvHotelId()) || "#N/A".equals(bean.getDotwHotelId())) {
                    errorDataList.add(bean);
                    continue;
                }
                WebbedsDaolvMapping webbedsDaolvMapping = WebbedsDaolvMapping.builder()
                        .daolvHotelId(Integer.valueOf(bean.getDaolvHotelId()))
                        .daolvHotelName(bean.getDaolvHotelName())
                        .dotwHotelCode(Integer.valueOf(bean.getDotwHotelId()))
                        .dotwHotelName(bean.getDotwHotelName())
                        .latitude(bean.getLatitude())
                        .longitude(bean.getLongitude())
                        .giataId(bean.getGiataId())
                        .mapped(bean.getMapped()).build();
                mappingList.add(webbedsDaolvMapping);
            }
            webbedsDaolvMappingDao.saveBatch(mappingList);
            mappingList.clear();
        }, 1000)).headRowNumber(2).sheet().doRead();

        System.out.println("错误的数据条数" + errorDataList.size());
        System.out.println("具体的数据" + errorDataList);
    }

    public void autoMatch() {
        Set<Integer> webbedsHotelIds = webbedsHotelDataDao.selectHotelIdList();
        Set<Integer> daolvHotelIds = jdJdbDaolvDao.selectHotelIdList();

        Set<Integer> webbedsMappingHotelIds = webbedsDaolvMappingDao.selectWebbedsHotelIdList();
        Set<Integer> daolvMappingHotelIds = webbedsDaolvMappingDao.selectDaolvHotelIdList();

        webbedsMappingHotelIds.retainAll(webbedsHotelIds);
        daolvMappingHotelIds.retainAll(daolvHotelIds);

        System.out.println("webbeds酒店id有效个数" + webbedsMappingHotelIds.size());
        System.out.println("daolv酒店id有效个数" + daolvMappingHotelIds.size());
    }

    public void matchTest() {
        List<WebbedsDaolvMapping> webbedsDaolvMappings = webbedsDaolvMappingDao.selectAll();

        List<WebbedsDaolvMatch> matchList = Lists.newArrayListWithExpectedSize(1000);
        List<Integer> daolvHotelIds = Lists.newArrayListWithExpectedSize(1000);
        List<Integer> dotwHotelCodes = Lists.newArrayListWithExpectedSize(1000);
        Map<Integer, Integer> daolvAndDotwMap = Maps.newHashMapWithExpectedSize(1000);
        //webbedsDaolvMappings = webbedsDaolvMappings.subList(0, 1200);
        for (int i = 0; i < webbedsDaolvMappings.size(); i++) {
            WebbedsDaolvMapping webbedsDaolvMapping = webbedsDaolvMappings.get(i);
            Integer daolvHotelId = webbedsDaolvMapping.getDaolvHotelId();
            Integer dotwHotelCode = webbedsDaolvMapping.getDotwHotelCode();
            daolvHotelIds.add(daolvHotelId);
            dotwHotelCodes.add(dotwHotelCode);
            daolvAndDotwMap.put(daolvHotelId, dotwHotelCode);
            if (i != 0 && (i + 1) % 1000 == 0) {
                batchMapping(matchList, daolvHotelIds, dotwHotelCodes, daolvAndDotwMap);
                matchList.clear();
                daolvHotelIds.clear();
                dotwHotelCodes.clear();
                daolvAndDotwMap.clear();
                continue;
            }
            if (i == webbedsDaolvMappings.size() - 1) {
                batchMapping(matchList, daolvHotelIds, dotwHotelCodes, daolvAndDotwMap);
            }
        }
    }

    private void batchMapping(List<WebbedsDaolvMatch> matchList, List<Integer> daolvHotelIds, List<Integer> dotwHotelCodes, Map<Integer, Integer> daolvAndDotwMap) {
        List<JdJdbGj> jdJdbGjList = jdJdbGjDao.selectInfoByIds(daolvHotelIds);
        Map<Integer, JdJdbGj> daolvHotelIdMap = jdJdbGjList.stream().collect(Collectors.toMap(JdJdbGj::getPlatId, Function.identity()));
        List<WebbedsHotelData> webbedsHotelDataList = webbedsHotelDataDao.selectInfoByIds(dotwHotelCodes);
        Map<Integer, WebbedsHotelData> dotwHotelCodeMap = webbedsHotelDataList.stream().collect(Collectors.toMap(WebbedsHotelData::getDotwHotelCode, Function.identity()));
        for (Map.Entry<Integer, Integer> entry : daolvAndDotwMap.entrySet()) {
            Integer daolvHotel = entry.getKey();
            Integer dotwHotel = entry.getValue();
            JdJdbGj jdJdbGj = daolvHotelIdMap.get(daolvHotel);
            WebbedsHotelData webbedsHotelData = dotwHotelCodeMap.get(dotwHotel);
            if (jdJdbGj == null) {
                WebbedsDaolvMatch webbedsDaolvMatch = null;/*WebbedsDaolvMatch.builder()
                        .daolvHotelId(daolvHotel)
                        .daolvHotelName(null)
                        .dotwHotelCode(dotwHotel)
                        .dotwHotelName(webbedsHotelData.getHotelName())
                        .webbedsLatitude(webbedsHotelData.getLatitude())
                        .webbedsLongitude(webbedsHotelData.getLongitude())
                        .daolvLatitude(null)
                        .daolvLongitude(null)
                        .diffMeter(null)
                        .nameMatch(-1).build();*/
                matchList.add(webbedsDaolvMatch);
                continue;
            }

            GlobalCoordinates daolv = new GlobalCoordinates(jdJdbGj.getLatitude().doubleValue(), jdJdbGj.getLongitude().doubleValue());
            GlobalCoordinates webbeds = new GlobalCoordinates(Double.parseDouble(webbedsHotelData.getLatitude()), Double.parseDouble(webbedsHotelData.getLongitude()));
            double diffMeter = getDistanceMeter(daolv, webbeds, Ellipsoid.WGS84);

            boolean b = webbedsHotelData.getHotelName().equals(jdJdbGj.getName()) || webbedsHotelData.getHotelName().startsWith(jdJdbGj.getName());
            WebbedsDaolvMatch webbedsDaolvMatch = null;/*WebbedsDaolvMatch.builder()
                    .daolvHotelId(daolvHotel)
                    .daolvHotelName(jdJdbGj.getName())
                    .dotwHotelCode(dotwHotel)
                    .dotwHotelName(webbedsHotelData.getHotelName())
                    .webbedsLatitude(webbedsHotelData.getLatitude())
                    .webbedsLongitude(webbedsHotelData.getLongitude())
                    .daolvLatitude(jdJdbGj.getLatitude().toString())
                    .daolvLongitude(jdJdbGj.getLongitude().toString())
                    .diffMeter(new BigDecimal(diffMeter))
                    .nameMatch(b ? 1 : 0).build();*/
            matchList.add(webbedsDaolvMatch);
        }
        webbedsDaolvMatchDao.saveBatch(matchList);
    }

    public double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid) {
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);
        return geoCurve.getEllipsoidalDistance();
    }

    @Transactional
    public void matchTest2() {
        List<WebbedsDaolvMatch> notMatchList = webbedsDaolvMatchDao.selectDaolv();
        List<Integer> daolvHotelIds = notMatchList.stream().map(WebbedsDaolvMatch::getDaolvHotelId).collect(Collectors.toList());
        List<JdJdbDaolv> jdJdbDaolvs = jdJdbDaolvDao.selectListByIds(daolvHotelIds);
        Map<Integer, JdJdbDaolv> hotelIdMap = jdJdbDaolvs.stream().collect(Collectors.toMap(JdJdbDaolv::getId, Function.identity()));

        for (WebbedsDaolvMatch webbedsDaolvMatch : notMatchList) {
            Integer daolvHotelId = webbedsDaolvMatch.getDaolvHotelId();
            JdJdbDaolv jdJdbDaolv = hotelIdMap.get(daolvHotelId);
            if (jdJdbDaolv == null) {
                continue;
            }
            GlobalCoordinates daolv = new GlobalCoordinates(jdJdbDaolv.getLatitude().doubleValue(), jdJdbDaolv.getLongitude().doubleValue());
            GlobalCoordinates webbeds = new GlobalCoordinates(Double.parseDouble(webbedsDaolvMatch.getWebbedsLatitude()), Double.parseDouble(webbedsDaolvMatch.getWebbedsLongitude()));
            double diffMeter = getDistanceMeter(daolv, webbeds, Ellipsoid.WGS84);

            boolean b = webbedsDaolvMatch.getDotwHotelName().equals(jdJdbDaolv.getName()) || webbedsDaolvMatch.getDotwHotelName().startsWith(jdJdbDaolv.getName());
            /*webbedsDaolvMatch.setDaolvHotelName(jdJdbDaolv.getName())
                    .setDaolvLatitude(jdJdbDaolv.getLatitude().toString())
                    .setDaolvLongitude(jdJdbDaolv.getLongitude().toString())
                    .setDiffMeter(new BigDecimal(diffMeter))
                    .setNameMatch(b ? 1 : 0);*/
            webbedsDaolvMatchDao.update(webbedsDaolvMatch);
            //System.out.println(webbedsDaolvMatch);
        }
    }

    public void matchTest3() throws Exception {
        //List<String> webbedsCountries = webbedsHotelDataDao.selectCountryCodes();
        //List<String> matchCountry = webbedsDaolvMatchLabCountDao.selectDownCountry();
        List<String> notFoundCountry = Lists.newArrayList();
        //List<String> skipCountry = Lists.newArrayList("MY", "MA", "ZA", "RU", "AT", "HR", "FR", "DE", "GR", "IT", "JP", "MX", "NL", "PL", "PT", "ES", "SE", "CH", "TR", "GB", "CN", "ID", "PH", "KR", "TW", "TH", "VN", "BR", "SA", "AR", "CO", "CA", "US", "AU", "NZ", "IN", "LK");
        List<String> skipCountry = Lists.newArrayList("MY");
        for (String countryCode : skipCountry) {
            // 已经匹配过的国家不再处理
        /*    if (matchCountry.contains(countryCode)) {
                continue;
            }
        */    StopWatch watch = new StopWatch();
            watch.start("国家" + countryCode);
            WebbedsDaolvMatchLabCount matchLabCount = new WebbedsDaolvMatchLabCount();
            List<WebbedsHotelData> webbedsHotelDataList = webbedsHotelDataDao.selectListByCountry(countryCode);
            List<JdJdbDaolv> jdJdbDaolvList = jdJdbDaolvDao.selectListByCountry(countryCode);
            // 找不到国家时，跳过本次循环
            if (CollectionUtils.isEmpty(jdJdbDaolvList)) {
                notFoundCountry.add(countryCode);
                continue;
            }
            Set<Integer> alreadyMatch = webbedsDaolvMatchDao.alreadySuccessMatchId();
            matchLabCount.setCountryCode(countryCode);
            matchLabCount.setWebbedsTotal(webbedsHotelDataList.size());
            // 过滤掉已mapping的酒店
            webbedsHotelDataList = webbedsHotelDataList.stream().filter(e -> !alreadyMatch.contains(e.getDotwHotelCode())).collect(Collectors.toList());
            matchLabCount.setWebbedsNeedMatchCount(webbedsHotelDataList.size());
            matchLabCount.setDaolvTotal(jdJdbDaolvList.size());

            Map<String, List<WebbedsHotelData>> webbedsDataMap = webbedsHotelDataList.stream().collect(Collectors.groupingBy(WebbedsHotelData::getHotelName));
            Map<String, List<JdJdbDaolv>> daolvDataMap = jdJdbDaolvList.stream().collect(Collectors.groupingBy(JdJdbDaolv::getName));

            List<WebbedsDaolvMatchLab> uniqueList = Lists.newArrayListWithCapacity(webbedsHotelDataList.size() * 2);
            List<WebbedsDaolvMatchLab> multiList = Lists.newArrayListWithCapacity(webbedsHotelDataList.size() * 2);
            Set<Integer> zeroScores = Sets.newHashSetWithExpectedSize(webbedsHotelDataList.size());
            for (Map.Entry<String, List<WebbedsHotelData>> entry : webbedsDataMap.entrySet()) {
                String hotelName = entry.getKey();
                long start = System.currentTimeMillis();
                //System.out.println("国家：" + countryCode + "酒店：" +  hotelName + "-计时开始" + start);
                //System.out.println("匹配第" + i + "家酒店，酒店名：" + hotelName + "还差" + (max - i));
                List<WebbedsHotelData> webbedsHotelList = entry.getValue();
                List<JdJdbDaolv> findDaolvHotels = daolvDataMap.get(hotelName);
                // 有同名酒店，计算得分
                if (CollectionUtils.isNotEmpty(findDaolvHotels)) {
                    findMatch(uniqueList, webbedsHotelList, findDaolvHotels, zeroScores, multiList);
                } else {
                    // 无同名酒店，全部匹配计算得分绝对值
                    findMatch(uniqueList, webbedsHotelList, jdJdbDaolvList, zeroScores, multiList);
                }
                System.out.println("国家" + countryCode + "酒店：" +  hotelName + "比对总耗时" + (System.currentTimeMillis() - start));
            }
            watch.stop();
            matchLabCount.setTime((int) watch.getTotalTimeSeconds());
            matchLabCount.setWebbedsNotScoreCount(zeroScores.size());
            matchLabCount.setWebbedsUniqueScoreCount((int) uniqueList.stream().map(WebbedsDaolvMatchLab::getDotwHotelCode).distinct().count());
            matchLabCount.setWebbedsMultiScoreCount((int) multiList.stream().map(WebbedsDaolvMatchLab::getDotwHotelCode).distinct().count());
            matchLabCount.setWebbedsScoreCount(matchLabCount.getWebbedsMultiScoreCount() + matchLabCount.getWebbedsUniqueScoreCount());
            //matchLabCount.setDaolvMatchedCount((int) uniqueList.stream().map(WebbedsDaolvMatchLab::getDaolvHotelId).distinct().count());
            webbedsDaolvMatchLabCountDao.insert(matchLabCount);
            saveBatch2(uniqueList);
            saveBatch3(multiList);
        }
        System.out.println("找不到这些国家" + notFoundCountry);
    }

    private void saveBatch2(List<WebbedsDaolvMatchLab> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<WebbedsDaolvMatchLab> list = insertList.subList(start, j);
                webbedsDaolvMatchLabDao.saveBatch(list);
                start = j;
            }
        }
        List<WebbedsDaolvMatchLab> list = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            webbedsDaolvMatchLabDao.saveBatch(list);
        }
    }
    private void saveBatch3(List<WebbedsDaolvMatchLab> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<WebbedsDaolvMatchLab> list = insertList.subList(start, j);
                webbedsDaolvMatchLabDao.saveBatch2(list);
                start = j;
            }
        }
        List<WebbedsDaolvMatchLab> list = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            webbedsDaolvMatchLabDao.saveBatch2(list);
        }
    }

    private void findMatch(List<WebbedsDaolvMatchLab> uniqueList, List<WebbedsHotelData> webbedsHotelList,
                           List<JdJdbDaolv> daolvHotels, Set<Integer> zeroScores,
                           List<WebbedsDaolvMatchLab> multiList) throws Exception {
        WebbedsHotelData webbedsHotelData1 = webbedsHotelList.get(0);
        long start = System.currentTimeMillis();
        for (WebbedsHotelData webbedsHotelData : webbedsHotelList) {
            List<CompletableFuture<CalculateResult>> futures = Lists.newArrayListWithCapacity(256);
            int size = daolvHotels.size();
            if (size < CORE_POOL_SIZE) {
                futures.add(executeOnceTask(webbedsHotelData, daolvHotels));
            } else {
                int chunkSize = (size / CORE_POOL_SIZE) + 1;
                for (int i = 0; i < daolvHotels.size(); i += chunkSize) {
                    final int endIndex = Math.min(i + chunkSize, daolvHotels.size());
                    futures.add(executeOnceTask(webbedsHotelData, daolvHotels.subList(i, endIndex)));
                }
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            Map<Integer, List<JdJdbDaolv>> scoreMap = Maps.newHashMapWithExpectedSize(265);
            for (CompletableFuture<CalculateResult> future : futures) {
                CalculateResult result = future.get();
                // 如果0分，跳过此部分评分
                if (result.getZero()) {
                    continue;
                }
                for (Map.Entry<Integer, List<JdJdbDaolv>> reslutEntry : result.getScoreMap().entrySet()) {
                    Integer score = reslutEntry.getKey();
                    List<JdJdbDaolv> sameScoreHotels = reslutEntry.getValue();
                    if (scoreMap.containsKey(score)) {
                        scoreMap.get(score).addAll(sameScoreHotels);
                    } else {
                        scoreMap.put(score, sameScoreHotels);
                    }
                }
            }
            System.out.println("国家：" + webbedsHotelData1.getShortCountryName() +
                    "酒店：" +  webbedsHotelData1.getHotelName() + "计分耗时" + (System.currentTimeMillis() - start));
            // 一个webbeds酒店，最终没有分数，计为0分
            if (scoreMap.isEmpty()) {
                zeroScores.add(webbedsHotelData.getDotwHotelCode());
                return;
            }
            // 一次结束后，能得到同名的最大得分的记录，唯一一条存入uniqueList，存在同分的多条存入multiList
            scoreMap.entrySet().stream()
                    .max(Comparator.comparingInt(entry1 -> Math.abs(entry1.getKey())))
                    .ifPresent(max -> createMatchResult(webbedsHotelData, max, uniqueList, multiList));
        }
    }

    private CompletableFuture<CalculateResult> executeOnceTask(WebbedsHotelData webbedsHotelData,List<JdJdbDaolv> daolvHotels) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Integer, List<JdJdbDaolv>> scoreMap = Maps.newConcurrentMap();
            for (JdJdbDaolv daolvHotel : daolvHotels) {
                Integer score = MappingScoreHelper.calculateScore(webbedsHotelData, daolvHotel);
                if (score == 0) continue;
                if (score == 16 || score == -16) {
                    CalculateResult result = new CalculateResult();
                    Map<Integer, List<JdJdbDaolv>> fullScoreMap = Maps.newConcurrentMap();
                    fullScoreMap.put(score, Lists.newArrayList(daolvHotel));
                    result.setScoreMap(fullScoreMap);
                    result.setZero(Boolean.FALSE);
                    return result;
                }
                if (scoreMap.containsKey(score)) {
                    scoreMap.get(score).add(daolvHotel);
                } else {
                    scoreMap.put(score, Lists.newArrayList(daolvHotel));
                }
            }
            CalculateResult result = new CalculateResult();
            result.setScoreMap(scoreMap);
            if (scoreMap.isEmpty()) {
                result.setZero(Boolean.TRUE);
            } else {
                result.setZero(Boolean.FALSE);
            }
            return result;
        }, executor);
    }

    private void createMatchResult(WebbedsHotelData webbedsHotelData, Map.Entry<Integer, List<JdJdbDaolv>> max,
                                   List<WebbedsDaolvMatchLab> uniqueList, List<WebbedsDaolvMatchLab> multiList) {
        Integer score = max.getKey();
        List<JdJdbDaolv> jdJdbDaolvs = max.getValue();
        if (jdJdbDaolvs.size() > 1) {
            for (JdJdbDaolv jdJdbDaolv : jdJdbDaolvs) {
                multiList.add(createOneMatchLab(webbedsHotelData, jdJdbDaolv, score, true));
            }
            return;
        }
        uniqueList.add(createOneMatchLab(webbedsHotelData, jdJdbDaolvs.get(0), score, false));
    }

    private WebbedsDaolvMatchLab createOneMatchLab(WebbedsHotelData webbedsHotelData, JdJdbDaolv jdJdbDaolv,
                                                   Integer score, boolean multiMatch) {
        Double meter = MappingScoreHelper.calculateMeter(webbedsHotelData, jdJdbDaolv);
        return null;/*WebbedsDaolvMatchLab.builder()
                .dotwHotelCode(webbedsHotelData.getDotwHotelCode())
                .dotwHotelName(webbedsHotelData.getHotelName())
                .webbedsCountry(webbedsHotelData.getShortCountryName())
                .webbedsLatitude(webbedsHotelData.getLatitude())
                .webbedsLongitude(webbedsHotelData.getLongitude())
                .webbedsAddress(webbedsHotelData.getHotelAddress())
                .webbedsTel(webbedsHotelData.getReservationTelephone())
                .daolvHotelId(jdJdbDaolv.getId())
                .daolvHotelName(jdJdbDaolv.getName())
                .daolvLatitude(jdJdbDaolv.getLatitude() == null ? null : jdJdbDaolv.getLatitude().toString())
                .daolvLongitude(jdJdbDaolv.getLongitude() == null ? null : jdJdbDaolv.getLongitude().toString())
                .daolvCountry(jdJdbDaolv.getCountryCode())
                .daolvAddress(jdJdbDaolv.getAddress())
                .daolvTel(jdJdbDaolv.getTelephone())
                .diffMeter(meter == null ? null : BigDecimal.valueOf(meter))
                .nameMatch(Math.abs(score) >= 10 ? 1 : 0)
                .addressMatch(Math.abs(score) == 5 || Math.abs(score) == 6 || Math.abs(score) == 15 || Math.abs(score) == 16 ? 1 : 0)
                .telMatch(Math.abs(score) == 1 || Math.abs(score) == 6 || Math.abs(score) == 11 || Math.abs(score) == 16 ? 1 : 0)
                .score(score)
                .multiMatch(multiMatch ? 1 : 0).build();*/
    }

    public void matchTest4() {
        List<String> webbedsCountries = webbedsHotelDataDao.selectCountryCodes();
        List<String> daolvCountries = jdJdbDaolvDao.selectCountryCodes();
        System.out.println(webbedsCountries.size() + "比对" + daolvCountries.size());
        webbedsCountries.removeAll(daolvCountries);
        System.out.println("找不到的" + webbedsCountries);

    }

    public void mapping() {
        // 导入12+6(5) 数据至zh_jd_jdb_gj_mapping表
        // 查询出分数等于16, 15, 11, -16, -15的酒店匹配数据
        List<WebbedsDaolvMatchLab> webbedsDaolvMatchLabList = webbedsDaolvMatchLabDao.selectHighScoreList();
        Map<Integer, List<WebbedsDaolvMatchLab>> daolvIdMap = webbedsDaolvMatchLabList.stream().collect(Collectors.groupingBy(WebbedsDaolvMatchLab::getDaolvHotelId));

        // 查询出webbeds给的经纬度访问在300米以内的酒店匹配数据
        List<WebbedsDaolvMatch> webbedsDaolvMatchList = webbedsDaolvMatchDao.selectEffectDataList();
        Map<Integer, List<WebbedsDaolvMatch>> daolvIdMap2 = webbedsDaolvMatchList.stream().collect(Collectors.groupingBy(WebbedsDaolvMatch::getDaolvHotelId));

        // 查询出当前的本地酒店映射表
        List<ZhJdJdbGjMapping> zhJdJdbGjMappingList = zhJdJdbGjMappingDao.selectAll();
        Map<String, Long> daolvExistMap = zhJdJdbGjMappingList.stream().collect(Collectors.toMap(ZhJdJdbGjMapping::getPlatId, ZhJdJdbGjMapping::getLocalId));


        List<ZhJdJdbGjMapping> insertList = Lists.newArrayListWithCapacity(webbedsDaolvMatchLabList.size() + webbedsDaolvMatchList.size());

        for (Map.Entry<Integer, List<WebbedsDaolvMatchLab>> entry : daolvIdMap.entrySet()) {
            String daolvId = entry.getKey().toString();
            List<WebbedsDaolvMatchLab> labs = entry.getValue();
            if (daolvExistMap.containsKey(daolvId)) {
                Long localId = daolvExistMap.get(daolvId);
                for (WebbedsDaolvMatchLab lab : labs) {
                    String dotwHotelCode = lab.getDotwHotelCode().toString();
                    ZhJdJdbGjMapping zhJdJdbGjMapping = new ZhJdJdbGjMapping();
                    zhJdJdbGjMapping.setLocalId(localId);
                    zhJdJdbGjMapping.setPlatId(dotwHotelCode);
                    zhJdJdbGjMapping.setPlat(2000071);
                    insertList.add(zhJdJdbGjMapping);
                }
            }
        }

        for (Map.Entry<Integer, List<WebbedsDaolvMatch>> entry : daolvIdMap2.entrySet()) {
            String daolvId = entry.getKey().toString();
            List<WebbedsDaolvMatch> matches = entry.getValue();
            if (daolvExistMap.containsKey(daolvId)) {
                Long localId = daolvExistMap.get(daolvId);
                for (WebbedsDaolvMatch match : matches) {
                    String dotwHotelCode = match.getDotwHotelCode().toString();
                    ZhJdJdbGjMapping zhJdJdbGjMapping = new ZhJdJdbGjMapping();
                    zhJdJdbGjMapping.setLocalId(localId);
                    zhJdJdbGjMapping.setPlatId(dotwHotelCode);
                    zhJdJdbGjMapping.setPlat(2000071);
                    insertList.add(zhJdJdbGjMapping);
                }
            }
        }
        //
        saveBatch4(insertList);
        System.out.println(insertList.size());
    }

    private void saveBatch4(List<ZhJdJdbGjMapping> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<ZhJdJdbGjMapping> list = insertList.subList(start, j);
                zhJdJdbGjMappingDao.insertBatch(list);
                start = j;
            }
        }
        List<ZhJdJdbGjMapping> list = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            zhJdJdbGjMappingDao.insertBatch(list);
        }

    }

}
