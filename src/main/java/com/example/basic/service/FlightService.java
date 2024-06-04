package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.basic.dao.BCityAirportDao;
import com.example.basic.dao.BCityDao;
import com.example.basic.dao.BClassDao;
import com.example.basic.domain.*;
import com.example.basic.entity.BCity;
import com.example.basic.entity.BCityAirport;
import com.example.basic.entity.BClass;
import com.example.basic.utils.PinyinUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/5/13
 */
@Slf4j
@Service
public class FlightService {

    @Resource
    private BCityDao bCityDao;

    @Resource
    private BCityAirportDao bCityAirportDao;

    @Resource
    private BClassDao bClassDao;

    @Transactional
    public void airportFresh() {
        List<BCityAirport> cityAirports = bCityAirportDao.selectAll();
        // 国际机场&城市
        cityAirports = cityAirports.stream().filter(e -> e.getInternational().equals(0)).collect(Collectors.toList());

        List<BCity> bCities = bCityDao.selectCityList();
        Map<String, List<BCity>> nameMap = bCities.stream().collect(Collectors.groupingBy(BCity::getCityName));
        Map<String, List<BCity>> enNameMap = bCities.stream().collect(Collectors.groupingBy(BCity::getEName));

        List<String> ids = Lists.newArrayListWithCapacity(cityAirports.size());
        List<BCityAirport> needUpdateList = Lists.newArrayListWithCapacity(cityAirports.size());
        for (BCityAirport cityAirport : cityAirports) {
            String cityName = cityAirport.getCityName();
            String enName = cityAirport.getCityEnName();
            List<BCity> nameCities = nameMap.get(cityName);
            if (CollectionUtils.isNotEmpty(nameCities) && nameCities.size() == 1) {
                BCity city = nameCities.get(0);
                String id = city.getId();
                ids.add(id);
                cityAirport.setCityId(id);
                needUpdateList.add(cityAirport);
                continue;
            }
            List<BCity> enNameCities = enNameMap.get(enName);
            if (CollectionUtils.isNotEmpty(enNameCities) && enNameCities.size() == 1) {
                BCity city = enNameCities.get(0);
                String id = city.getId();
                ids.add(id);
                cityAirport.setCityId(id);
                needUpdateList.add(cityAirport);
            }
        }
        System.out.println("匹配到ids" + ids.size() + ids);
        System.out.println("待更新机场数据" + needUpdateList.size());
        bCityDao.updateAirport(ids);
        for (BCityAirport bCityAirport : needUpdateList) {
            bCityAirportDao.updateCityId(bCityAirport);
        }
    }

    public void airportUpdate() throws FileNotFoundException {
        String fileName = "C:\\wst_han\\打杂\\基础数据\\基础数据.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        Map<String, String> threeCodeAirportMap = Maps.newHashMapWithExpectedSize(5000);
        EasyExcel.read(inputStream, AirportUpdateBean.class, new PageReadListener<AirportUpdateBean>(dataList -> {
            for (AirportUpdateBean oneLine : dataList) {
                log.info("读取到一条数据{}", oneLine);
            }
            for (AirportUpdateBean airportUpdateBean : dataList) {
                String threeCode = airportUpdateBean.getThreeCode();
                String cityId = airportUpdateBean.getCityId();
                if (StringUtils.hasLength(cityId) && !threeCodeAirportMap.containsKey(threeCode)) {
                    threeCodeAirportMap.put(threeCode, cityId);
                }
            }
            //threeCodeAirportMap.putAll(dataList.stream().filter(e->StringUtils.hasLength(e.getCityId())).collect(Collectors.toMap(AirportUpdateBean::getThreeCode, AirportUpdateBean::getCityId)));

        }, 1000)).headRowNumber(1).sheet().doRead();
        System.out.println(threeCodeAirportMap);
        Collection<String> values = threeCodeAirportMap.values();
        List<String> ids = Lists.newArrayList(values);
        bCityDao.updateAirport(ids);
        Set<String> threeCodes = threeCodeAirportMap.keySet();
        List<BCityAirport> cityAirports = bCityAirportDao.selectListByThreeCodes(threeCodes);
        for (BCityAirport cityAirport : cityAirports) {
            String threeCode = cityAirport.getThreeCode();
            String cityId = threeCodeAirportMap.get(threeCode);
            cityAirport.setCityId(cityId);
            bCityAirportDao.updateCityId(cityAirport);
        }
    }

    public void airportUpdateName() throws FileNotFoundException {
        String fileName = "C:\\wst_han\\打杂\\基础数据\\IBE机场数据.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        Map<String, String> threeCodeAirportNameMap = Maps.newHashMapWithExpectedSize(6000);
        EasyExcel.read(inputStream, AirportUpdateNameBean.class, new PageReadListener<AirportUpdateNameBean>(dataList -> {
            threeCodeAirportNameMap.putAll(dataList.stream().filter(e->StringUtils.hasLength(e.getAirportEnName()) && e.getAirportEnName().endsWith("Airport")).collect(Collectors.toMap(AirportUpdateNameBean::getThreeCode, AirportUpdateNameBean::getAirportEnName)));

        }, 1000)).headRowNumber(0).sheet().doRead();
        System.out.println(threeCodeAirportNameMap);

        //List<BCityAirport> cityAirports = bCityAirportDao.selectAll();
        // 国际机场&城市
       // cityAirports = cityAirports.stream().filter(e -> e.getInternational().equals(0)).collect(Collectors.toList());

        /*for (BCityAirport cityAirport : cityAirports) {
            String threeCode = cityAirport.getThreeCode();
            String airportEnName = threeCodeAirportNameMap.get(threeCode);
            if (!StringUtils.hasLength(airportEnName)) {
                continue;
            }
            cityAirport.setEName(airportEnName);
           // System.out.println("更新" + threeCode + "机场，英文名字为：" + airportEnName);
            bCityAirportDao.updateAirportEnName(cityAirport);
        }*/
    }

    public void airportTest() {
        List<BCityAirport> cityAirports = bCityAirportDao.selectAll();
    }

    public void airportUpdate1() throws FileNotFoundException {
        String fileName = "C:\\wst_han\\打杂\\基础数据\\基础数据.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
//        Map<String, String> threeCodeAirportMap = Maps.newHashMapWithExpectedSize(5000);
        List<String> threeCodeAirportLIst = Lists.newArrayList();
        EasyExcel.read(inputStream, AirportUpdateBean.class, new PageReadListener<AirportUpdateBean>(dataList -> {
            for (AirportUpdateBean oneLine : dataList) {
                log.info("读取到一条数据{}", oneLine);
                String remark = oneLine.getRemark();
                if (StringUtils.hasLength(remark) && !"携程无机场".equals(remark) && !"该城市无机场".equals(remark)) {
                    threeCodeAirportLIst.add(oneLine.getThreeCode());
                }
            }

            //threeCodeAirportMap.putAll(dataList.stream().filter(e->StringUtils.hasLength(e.getCityId())).collect(Collectors.toMap(AirportUpdateBean::getThreeCode, AirportUpdateBean::getCityId)));

        }, 1000)).headRowNumber(1).sheet().doRead();
        System.out.println(threeCodeAirportLIst);
        //bCityDao.updateType(threeCodeAirportLIst);
        /*List<BCityAirport> cityAirports = bCityAirportDao.selectAll();
        cityAirports = cityAirports.stream().filter(e -> threeCodeAirportLIst.contains(e.getThreeCode())).collect(Collectors.toList());
        System.out.println(cityAirports);
        // 723655
        List<BCity> oldList = bCityDao.selectOldList(cityAirports.stream().map(BCityAirport::getCityId).toList());
        List<BCity> newList = Lists.newArrayList();
        int id = 723656;
        for (int i = 0; i < oldList.size(); i++) {
            BCity newCity = new BCity();
            BeanUtils.copyProperties(oldList.get(i), newCity);
            newCity.setId(String.valueOf(id + i));
            newList.add(newCity);
        }
        System.out.println(newList);*/


    }

    @Transactional
    public void airport() {
        int start = 723656;
        Date date = new Date();
        List<BCity> insertList = Lists.newArrayListWithCapacity(200);
        Map<String, String> updateMap = Maps.newHashMapWithExpectedSize(200);
        List<BCityAirport> bCityAirports = bCityAirportDao.selectList();
        List<BCityAirport> noCityList = bCityAirports.stream().filter(e -> !StringUtils.hasLength(e.getCityId()) || !StringUtils.hasLength(e.getCityName())).collect(Collectors.toList());
        List<BCityAirport> cityList = bCityAirports.stream().filter(e -> StringUtils.hasLength(e.getCityId()) && StringUtils.hasLength(e.getCityName())).collect(Collectors.toList());
        for (BCityAirport bCityAirport : cityList) {
            String cityName = bCityAirport.getCityName();
            String cityEnName = bCityAirport.getCityEnName();
            String nation = bCityAirport.getNation();
            BCity bCity = new BCity();
            bCity.setId(String.valueOf(start));
            bCity.setCityName(cityName);
            bCity.setEName(cityEnName);
            bCity.setInternational(0);
            bCity.setCityType(0);
            bCity.setIsAirport(1);
            bCity.setIsTrainStation(0);
            bCity.setNation(nation);
            bCity.setProvince(null);
            bCity.setIntroduce(null);
            bCity.setElongId(null);
            bCity.setPyjsm(PinyinUtil.getPinYinHeadChar(bCity.getCityName()).toUpperCase());
            bCity.setFullspell(PinyinUtil.converterToSpell(bCity.getCityName()).toUpperCase());
            bCity.setCreatedate(date);
            bCity.setRemark("20240520机场数据反导入");
            insertList.add(bCity);
            updateMap.put(String.valueOf(start), bCityAirport.getId());
            //bCityAirportDao.updateNewCityId(start, bCityAirport.getId());
            start++;
        }
        Map<String, String> map = Maps.newHashMap();
        map.put("BQK","布劳恩斯魏克,美国,0003002");
        map.put("CHY","乔依绍尔湾,所罗门群岛,0006011");
        map.put("CIU","苏珊玛利,美国,0003002");
        map.put("CTL","查理维尔,澳大利亚,0006001");
        map.put("DCF","多米尼加岛,多米尼克,0006224");
        map.put("EUX","圣奥依斯坦图斯,安的列斯,0006248");
        map.put("FAV","法卡拉瓦岛,波利尼西亚,0002048");
        map.put("FEN","费.努罗尼亚,巴西,0004003");
        map.put("FUE","富埃特文图拉岛,西班牙,0002040");
        map.put("IUE","纽埃岛,纽埃,0006242");
        map.put("KSA","科斯雷,密克罗尼西亚,0006006");
        map.put("LEC","雷恩克斯,巴西,0004003");
        map.put("NLK","诺福克岛,诺福克岛,0006239");
        map.put("NUX","诺维乌仍果,俄罗斯,0002035");
        map.put("ONJ","大馆能代,日本,0001027");
        map.put("PIX","皮克岛,葡萄牙,0002033");
        map.put("PNI","波纳佩,密克罗尼西亚,0006006");
        map.put("RFP","雷伊提,波利尼西亚,0002048");
        map.put("RGI","朗伊罗阿,波利尼西亚,0002048");
        map.put("SDY","西德尼,美国,0003002");
        map.put("SHC","印达萨拉西,埃萨俄比亚,0005003");
        map.put("SSH","沙姆沙伊赫湾,埃及,0005002");
        map.put("SXM","圣马滕,荷属圣马丁,0006263");
        map.put("XMH","曼尼提,波利尼西亚,0002048");
        map.put("ZIG","济金绍尔,塞内加尔,0005041");
        for (BCityAirport bCityAirport : noCityList) {
            String nameString = map.get(bCityAirport.getThreeCode());
            String[] splits = nameString.split(",");
            String cityName = splits[0];
            String cityEnName = null;
            String nation = splits[2];
            BCity bCity = new BCity();
            bCity.setId(String.valueOf(start));
            bCity.setCityName(cityName);
            bCity.setEName(cityEnName);
            bCity.setInternational(0);
            bCity.setCityType(0);
            bCity.setIsAirport(1);
            bCity.setIsTrainStation(0);
            bCity.setNation(nation);
            bCity.setProvince(null);
            bCity.setIntroduce(null);
            bCity.setElongId(null);
            bCity.setPyjsm(PinyinUtil.getPinYinHeadChar(bCity.getCityName()).toUpperCase());
            bCity.setFullspell(PinyinUtil.converterToSpell(bCity.getCityName()).toUpperCase());
            bCity.setCreatedate(date);
            bCity.setRemark("20240520机场数据反导入");
            insertList.add(bCity);
            updateMap.put(String.valueOf(start), bCityAirport.getId());
            //bCityAirportDao.updateNewCityId(start, bCityAirport.getId());
            start++;
        }
        bCityDao.saveBatch(insertList);
        for (Map.Entry<String, String> entry : updateMap.entrySet()) {
            bCityAirportDao.updateNewCityId(entry.getKey(), entry.getValue());
        }
//        System.out.println(insertList);
//        System.out.println(updateMap.entrySet());
    }

    public void airportFind() {
        List<BCityAirport> notFindList = bCityAirportDao.selectWantedList();
        List<BCityAirport> canNotMatchList = notFindList.stream().filter(e -> !StringUtils.hasLength(e.getCityId()) || !StringUtils.hasLength(e.getCityName())).toList();
        List<BCityAirport> otherList = notFindList.stream().filter(e -> StringUtils.hasLength(e.getCityId()) && StringUtils.hasLength(e.getCityName())).toList();
        List<String> countryList = otherList.stream().map(BCityAirport::getNation).distinct().toList();
        List<BCity> bCities = bCityDao.selectCityListByCountries(countryList);
        Map<String, List<BCity>> collect = bCities.stream().collect(Collectors.groupingBy(BCity::getCityName));

        Map<String, String> findOnlyMap = Maps.newHashMap();
        for (BCityAirport bCityAirport : otherList) {
            String cityName = bCityAirport.getCityName();
            String nation = bCityAirport.getNation();
            if (collect.containsKey(cityName)) {
                List<BCity> bCities1 = collect.get(cityName);
                if (bCities1.size() == 1 && nation.equals(bCities1.get(0).getNation())) {
                    findOnlyMap.put(bCities1.get(0).getId(), bCityAirport.getId());
                }
            }
        }

        System.out.println("总数" + notFindList.size());
        System.out.println("无法匹配的记录" + canNotMatchList.size() + "占比" + (double)canNotMatchList.size() / notFindList.size() * 100);
        System.out.println("有city的总数" + otherList.size() + "占比" + (double)otherList.size() / notFindList.size() * 100);
        System.out.println("有city切能找到唯一城市的" + findOnlyMap.size());
    }

    @Transactional
    public void airportUpdate2() throws FileNotFoundException {
        Map<String, String> updateMap = Maps.newHashMapWithExpectedSize(1500);

        String fileName = "C:\\wst_han\\打杂\\基础数据\\IBE机场数据.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<AirportUpdate> airportUpdates = Lists.newArrayListWithCapacity(8820);
        EasyExcel.read(inputStream, AirportUpdate.class, new PageReadListener<AirportUpdate>(airportUpdates::addAll, 1000)).headRowNumber(0).sheet().doRead();
        System.out.println(airportUpdates.size());
        Map<String, AirportUpdate> collect = airportUpdates.stream().filter(e->e.getAirportEnName().endsWith("Airport")).collect(Collectors.toMap(AirportUpdate::getThreeCode, Function.identity()));
        List<BCityAirport> notFindList = bCityAirportDao.selectWantedList();
        List<BClass> bClasses = bClassDao.selectCountryList();
        Map<String, String> nationIdAndCountryCodeMap = bClasses.stream().collect(Collectors.toMap(BClass::getId, BClass::getBy3));
        List<BCity> bCities = bCityDao.selectList();
        Map<String, List<BCity>> map = bCities.stream().collect(Collectors.groupingBy(BCity::getCityName));

        List<String> realMatchList = Lists.newArrayList();
        List<String> needDeleteList = Lists.newArrayList();

        for (BCityAirport bCityAirport : notFindList) {
            String id = bCityAirport.getId();
            String threeCode = bCityAirport.getThreeCode();
            AirportUpdate airportUpdate = collect.get(threeCode);
            if (airportUpdate ==null) {
                needDeleteList.add(threeCode);
                continue;
            }
            String cityName = airportUpdate.getCityName();
            String countryCode = airportUpdate.getCountryCode();
            if (map.containsKey(cityName)) {
                List<BCity> bCities1 = map.get(cityName);
                if (bCities1.size() == 1) {
                    realMatchList.add(cityName);
                    updateMap.put(id, bCities1.get(0).getId());
                    continue;
                }
                boolean find = false;
                for (BCity bCity : bCities1) {
                    String nation = bCity.getNation();
                    String code = nationIdAndCountryCodeMap.get(nation);
                    if (code.endsWith(countryCode)) {
                        realMatchList.add(cityName);
                        updateMap.put(id, bCity.getId());
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    needDeleteList.add(threeCode);
                }
            }else {
                needDeleteList.add(threeCode);
            }
        }
        Date date = new Date();
        List<String> keep = Lists.newArrayList("TCI", "MKF", "TSE");
        Map<String, String> mmap = Maps.newHashMap();
        mmap.put("TCI","TENERIFE,特内里费,0002040,753851");
        mmap.put("MKF","MUENSTER/OSNABRUECK,MUENSTER/OSNABRUECK,0002017,8385266221");
        mmap.put("TSE","NUR SULTAN,阿斯塔纳,0001013,753711");

        needDeleteList.removeAll(keep);
        List<BCity> insertList = Lists.newArrayList();
        int start = 723780;
        for (Map.Entry<String, String> entry : mmap.entrySet()) {
            String value = entry.getValue();
            String[] split = value.split(",");
            String cityName = split[1];
            String cityEnName = split[0];
            BCity bCity = new BCity();
            bCity.setId(String.valueOf(start));
            bCity.setCityName(cityName);
            bCity.setEName(cityEnName);
            bCity.setInternational(0);
            bCity.setCityType(0);
            bCity.setIsAirport(1);
            bCity.setIsTrainStation(0);
            bCity.setNation(split[2]);
            bCity.setProvince(null);
            bCity.setIntroduce(null);
            bCity.setElongId(null);
            bCity.setPyjsm(PinyinUtil.getPinYinHeadChar(bCity.getCityName()).toUpperCase());
            bCity.setFullspell(PinyinUtil.converterToSpell(bCity.getCityName()).toUpperCase());
            bCity.setCreatedate(date);
            bCity.setRemark("20240520机场数据反导入");
            insertList.add(bCity);
            updateMap.put(String.valueOf(start), split[3]);
            start++;
        }
        System.out.println("总数"+ notFindList.size());
        System.out.println("cityName真找到城市"+ realMatchList.size());
        System.out.println("准删除列表"+ needDeleteList.size() + needDeleteList);
        System.out.println("新增城市数据"+ insertList);
        System.out.println("更新机场数据"+ updateMap);

        /*bCityDao.saveBatch(insertList);
        for (Map.Entry<String, String> entry : updateMap.entrySet()) {
            bCityAirportDao.updateNewCityId(entry.getValue(), entry.getKey());
        }*/
    }

    public void airportUpdate3() throws FileNotFoundException {
        String fileName = "C:\\wst_han\\打杂\\基础数据\\IBE机场数据.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<AirportUpdate3> airportUpdates = Lists.newArrayListWithCapacity(10000);
        EasyExcel.read(inputStream, AirportUpdate3.class, new PageReadListener<AirportUpdate3>(airportUpdates::addAll, 1000)).headRowNumber(0).sheet().doRead();

        List<BCityAirport> cityAirports = bCityAirportDao.selectAll();
        Map<String, BCityAirport> oldCodeListMap = cityAirports.stream().collect(Collectors.toMap(BCityAirport::getThreeCode, Function.identity()));

        List<CityData> cityDataList = bCityDao.selectDataList();
        Map<String, List<CityData>> countryCodeCityMap = cityDataList.stream().collect(Collectors.groupingBy(CityData::getCountryCode));
        Map<String, String> countryCodeIdMap = cityDataList.stream().collect(Collectors.toMap(CityData::getCountryCode, CityData::getNationId, (k1, k2)-> k2));

        List<BCityAirport> insertCity = Lists.newArrayList();
        List<BCityAirport> insertAirport = Lists.newArrayList();



        int start = 755089;//763900
        Date date = new Date();
        for (int i = 0; i < airportUpdates.size(); i++) {
            AirportUpdate3 airportUpdate = airportUpdates.get(i);
            String threeCode = airportUpdate.getThreeCode();
            String cityName = airportUpdate.getCityName();
            String cityEnName = airportUpdate.getCityEnName();
            String airportHumpName = airportUpdate.getCityOrAirportHumpName();
            BCityAirport cityAirport = oldCodeListMap.get(threeCode);

            if (cityAirport != null) {
                // update
                // ICN 需要跳过
                if (!airportHumpName.endsWith("Airport")) {
                    String countryCode = airportUpdate.getCountryCode();
                    List<CityData> cityData = countryCodeCityMap.get(countryCode);
                    if (cityData == null) {
                        insert(insertCity, start, date, i, airportUpdate, threeCode, 2, null, null);
                        continue;
                    }
                    List<CityData> findCity = cityData.stream().filter(e -> cityName.equals(e.getCityName()) || cityEnName.equals(e.getCityEnName())).collect(Collectors.toList());
                    String cityId = null;
                    if (CollectionUtils.isNotEmpty(findCity) && findCity.size() == 1) {
                        cityId = findCity.get(0).getCityId();
                    }
                    if (cityId == null && "CN".equals(countryCode)) {
                        findCity = cityData.stream().filter(e -> StringUtils.hasLength(e.getCityName()) && e.getCityName().startsWith(cityName)).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(findCity) && findCity.size() == 1) {
                            cityId = findCity.get(0).getCityId();
                        }
                    }
                    insert(insertCity, start, date, i, airportUpdate, threeCode, 2, cityId, countryCodeIdMap.get(countryCode));
                }
                continue;
            }
            String countryCode = airportUpdate.getCountryCode();
            List<CityData> cityData = countryCodeCityMap.get(countryCode);
            if (cityData == null) {
                // 机场
                if (airportHumpName.endsWith("Airport")) {
                    // insert airport
                    insert(insertAirport, start, date, i, airportUpdate, threeCode, 1, null,null);

                } else {
                    // 城市
                    // insert city
                    insert(insertCity, start, date, i, airportUpdate, threeCode, 2, null, null);
                }
                continue;
            }
            List<CityData> findCity = cityData.stream().filter(e -> cityName.equals(e.getCityName()) || cityEnName.equals(e.getCityEnName())).collect(Collectors.toList());
            String cityId = null;
            if (CollectionUtils.isNotEmpty(findCity) && findCity.size() == 1) {
                cityId = findCity.get(0).getCityId();
            }
            if (cityId == null && "CN".equals(countryCode)) {
                findCity = cityData.stream().filter(e -> StringUtils.hasLength(e.getCityName()) && e.getCityName().startsWith(cityName)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(findCity) && findCity.size() == 1) {
                    cityId = findCity.get(0).getCityId();
                }
            }
            // 机场
            if (airportHumpName.endsWith("Airport")) {
                // insert airport
                insert(insertAirport, start, date, i, airportUpdate, threeCode, 1, cityId, countryCodeIdMap.get(countryCode));
            } else {
                // 城市
                // insert city
                insert(insertCity, start, date, i, airportUpdate, threeCode, 2, cityId, countryCodeIdMap.get(countryCode));
            }

        }
        insertAirport.addAll(insertCity);
        bCityAirportDao.saveBatch(insertAirport);
        /*System.out.println(insertAirport.size());
        System.out.println(insertCity.size());*/
    }

    private void insert(List<BCityAirport> insert, int start, Date date, int i, AirportUpdate3 airportUpdate, String threeCode, int type, String cityId, String nationId) {
        BCityAirport bCityAirport = new BCityAirport();
        bCityAirport.setId(String.valueOf(start + i));
        bCityAirport.setCityId(cityId);
        bCityAirport.setAirportName(airportUpdate.getCityOrAirportName());
        bCityAirport.setEName(airportUpdate.getCityOrAirportSmallEnName());
        bCityAirport.setThreeCode(threeCode);
        bCityAirport.setInternational(airportUpdate.getCountryCode().equals("CN") ? 1 : 0);
        bCityAirport.setPyjsm(PinyinUtil.getPinYinHeadChar(bCityAirport.getAirportName()).toUpperCase());
        bCityAirport.setFullspell(PinyinUtil.converterToSpell(bCityAirport.getAirportName()).toUpperCase());
        bCityAirport.setIsGk(0);
        bCityAirport.setType(type);
        bCityAirport.setCreatedate(date);
        bCityAirport.setRemark("20240524IBE文件导入");
        bCityAirport.setNation(nationId);
        bCityAirport.setNationCode(airportUpdate.getCountryCode());
        bCityAirport.setNationName(airportUpdate.getCountryName());
        bCityAirport.setCityName(airportUpdate.getCityName());
        bCityAirport.setCityEnName(airportUpdate.getCityEnName());
        insert.add(bCityAirport);
    }
}
