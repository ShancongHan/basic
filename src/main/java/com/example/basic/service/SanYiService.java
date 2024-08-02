package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.util.StringUtils;
import com.example.basic.dao.*;
import com.example.basic.domain.NearByResult;
import com.example.basic.domain.SanYiExcelDataBean;
import com.example.basic.domain.WebbedsImportBean;
import com.example.basic.entity.BClass;
import com.example.basic.entity.JdJdbGj;
import com.example.basic.entity.SanyiData;
import com.example.basic.entity.SanyiNearHotel;
import com.example.basic.helper.CalculatingDistanceHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/7/9
 */
@Slf4j
@Service
public class SanYiService {

    @Resource
    private BCityDao bCityDao;

    @Resource
    private SanyiDataDao sanyiDataDao;

    @Resource
    private BClassDao bClassDao;

    @Resource
    private JdJdbGjDao jdJdbGjDao;

    @Resource
    private SanyiNearHotelDao sanyiNearHotelDao;

    private static final Integer CORE_POOL_SIZE = 100;
    private static final Double LIMIT_METERS = 5000D;
    private static final Integer MAXIMUM_POOL_SIZE = 120;

    private static final Executor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(3000));

    public void findHotel() throws FileNotFoundException {
        String fileName = "C:\\wst_han\\打杂\\国区行政后勤资源盘点 Inventory of Oversea Admin Resources 0415(2).xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<SanYiExcelDataBean> dataBeans = Lists.newArrayList();
        EasyExcel.read(inputStream, SanYiExcelDataBean.class, new PageReadListener<SanYiExcelDataBean>(dataBeans::addAll, 1000)).headRowNumber(1).sheet().doRead();
        System.out.println( "总数" + dataBeans.size());
        List<SanYiExcelDataBean> realList = dataBeans.stream().filter(e -> StringUtils.isNotBlank(e.getLatitude()) && StringUtils.isNotBlank(e.getLatitude())).toList();
        System.out.println( "有经纬度的地址数据" + realList.size());

        List<String> countryNames = realList.stream().map(SanYiExcelDataBean::getCountry).distinct().toList();
        System.out.println( "数据分布国家数" + countryNames.size());
        List<BClass> countryList = bClassDao.selectCountryList();
        Map<String, String> countryNameIdMap = countryList.stream().collect(Collectors.toMap(BClass::getCName, BClass::getId));

        List<SanyiData> sanyiDataList = Lists.newArrayList();
        for (SanYiExcelDataBean sanYiExcelDataBean : realList) {
            String countryName = sanYiExcelDataBean.getCountry();
            String nationId = countryNameIdMap.get(countryName);
            SanyiData data = new SanyiData();
            BeanUtils.copyProperties(sanYiExcelDataBean,data);
            data.setCountryCode(nationId);
            data.setCountryMapped("1");
            data.setNo(Integer.valueOf(sanYiExcelDataBean.getNo()));
            sanyiDataList.add(data);
        }
        sanyiDataDao.saveBatch(sanyiDataList);
    }

    public void findCity() {
        List<SanyiData> sanyiDataList = sanyiDataDao.selectAll();
        for (SanyiData sanyiData : sanyiDataList) {
            String countryCode = sanyiData.getCountryCode();
            String cityName = sanyiData.getCity();
            List<String> cityIds = bCityDao.selectCityId(countryCode, cityName);
            if (CollectionUtils.isNotEmpty(cityIds)) {
                sanyiData.setCityId(String.join(",", cityIds));
                sanyiData.setCityMapped("1");
                sanyiDataDao.update(sanyiData);
            }
        }
    }

    public void findHotel2()  throws Exception {
        List<SanyiData> sanyiDataList = sanyiDataDao.selectAll();
        List<SanyiNearHotel> nearHotels = Lists.newArrayList();
        for (SanyiData sanyiData : sanyiDataList) {
            String countryCode = sanyiData.getCountryCode();
            String cityId = sanyiData.getCityId();
            List<JdJdbGj> lookupRange;
            // cityId唯一
            if (StringUtils.isNotBlank(cityId) && !cityId.contains(",")) {
                lookupRange = jdJdbGjDao.selectPartByCountryAndCityId(countryCode, cityId);
            }else {
                lookupRange = jdJdbGjDao.selectPartByCountryAndCityId(countryCode, null);
            }
            Map<Double, List<JdJdbGj>> map = findNearByHotel(sanyiData, lookupRange);
            for (Map.Entry<Double, List<JdJdbGj>> entry : map.entrySet()) {
                Double meters = entry.getKey();
                List<JdJdbGj> value = entry.getValue();
                for (JdJdbGj jdJdbGj : value) {
                    SanyiNearHotel near = new SanyiNearHotel();
                    BeanUtils.copyProperties(sanyiData, near);
                    near.setDiffMeter(BigDecimal.valueOf(meters));
                    near.setHotelId(jdJdbGj.getId());
                    near.setHotelName(jdJdbGj.getNameCn());
                    near.setHotelNameOriginal(jdJdbGj.getName());
                    near.setStarRating(jdJdbGj.getStarRating());
                    near.setHotelAddress(jdJdbGj.getAddress());
                    near.setHotelCountryCode(jdJdbGj.getCountryCode());
                    near.setHotelCountryName(jdJdbGj.getCountryNameCn());
                    near.setHotelCityId(jdJdbGj.getCityId());
                    near.setHotelCityName(jdJdbGj.getCityNameCn());
                    near.setHotelLatitude(jdJdbGj.getLatitude());
                    near.setHotelLongitude(jdJdbGj.getLongitude());
                    near.setTelephone(jdJdbGj.getTelephone());
                    nearHotels.add(near);
                }
            }
        }
        //System.out.println(nearHotels);
        sanyiNearHotelDao.saveBatch(nearHotels);
    }

    private Map<Double, List<JdJdbGj>> findNearByHotel(SanyiData sanyiData, List<JdJdbGj> lookupRange) throws Exception {
        List<CompletableFuture<NearByResult>> futures = Lists.newArrayListWithCapacity(256);
        int size = lookupRange.size();
        if (size < CORE_POOL_SIZE) {
            futures.add(executeOnceTask(sanyiData, lookupRange));
        } else {
            int chunkSize = (size / CORE_POOL_SIZE) + 1;
            for (int i = 0; i < lookupRange.size(); i += chunkSize) {
                final int endIndex = Math.min(i + chunkSize, lookupRange.size());
                futures.add(executeOnceTask(sanyiData, lookupRange.subList(i, endIndex)));
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        Map<Double, List<JdJdbGj>> metersMap = Maps.newHashMapWithExpectedSize(265);
        for (CompletableFuture<NearByResult> future : futures) {
            NearByResult result = future.get();
            metersMap.putAll(result.getMetersMap());
        }
        return metersMap;
    }


    private CompletableFuture<NearByResult> executeOnceTask(SanyiData sanyiData, List<JdJdbGj> jdJdbGjs) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Double, List<JdJdbGj>> metersMap = Maps.newConcurrentMap();
            for (JdJdbGj jdJdbGj : jdJdbGjs) {
                if (jdJdbGj.getLatitude() == null || jdJdbGj.getLongitude() == null) continue;
                double meters = CalculatingDistanceHelper.calculateMeters(sanyiData.getLatitude(), sanyiData.getLongitude(), jdJdbGj.getLatitude(), jdJdbGj.getLongitude());
                // 5KM以内，计入结果
                if (LIMIT_METERS.compareTo(meters) > 0) {
                    if (metersMap.containsKey(meters)) {
                        metersMap.get(meters).add(jdJdbGj);
                    } else {
                        metersMap.put(meters, Lists.newArrayList(jdJdbGj));
                    }
                }
            }
            NearByResult result = new NearByResult();
            result.setMetersMap(metersMap);
            return result;
        }, executor);
    }

    public void export() {
        List<SanyiNearHotel> sanyiNearHotels = sanyiNearHotelDao.selectAll();
        String fileName = "C:\\wst_han\\打杂\\三一临近酒店.xlsx";
        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, SanyiNearHotel.class).sheet("临近酒店数据").doWrite(sanyiNearHotels);
    }
}
