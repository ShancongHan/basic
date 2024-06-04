package com.example.basic.service;

import com.example.basic.dao.*;
import com.example.basic.entity.*;
import com.example.basic.utils.PinyinUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author han
 * @since 2024/5/10
 */
@Service
public class BasicDataService {

    @Resource
    private BCityDao bCityDao;

    @Resource
    private BClassDao bClassDao;

    @Resource
    private GeoProvinceDao geoProvinceDao;

    @Resource
    private JdGlobalGeoDao jdGlobalGeoDao;

    @Resource
    private BCityAirportDao bCityAirportDao;

    private final static Map<String, String> ZZGX_MAP = Maps.newHashMap();
    private final static List<String> CHINA_LIST = Lists.newArrayList("00002", "02032", "02033", "02034");


    static {
        ZZGX_MAP.put("/1/", "14201");//亚洲
        ZZGX_MAP.put("/2/", "14203");//北美洲
        ZZGX_MAP.put("/3/", "14204");//南美洲
        ZZGX_MAP.put("/4/", "14205");//非洲
        ZZGX_MAP.put("/5/", "14202");//欧洲
        ZZGX_MAP.put("/6/", "14206");//大洋洲
    }

    /*
     * 1. 比较国家
     * 2. 比较省份
     * 3. 比较城市
     * */
    public void compare() {
        // 1. 比较国家
        // 数量、匹配百分比
        // b_class 国际二字码缺失补充sql如下
        // UPDATE b_class SET by3 = 'RS' where par_no = '101' and c_name = '塞尔维亚';
        // UPDATE b_class SET by3 = 'CR' where par_no = '101' and c_name = '哥斯达黎加';
        // UPDATE b_class SET by3 = 'YK' where par_no = '101' and c_name = '科索沃';
        // UPDATE b_class SET by3 = 'ME' where par_no = '101' and c_name = '黑山';
        // UPDATE b_class SET by3 = 'PF' where par_no = '101' and c_name = '法属波利尼西亚';
        List<BClass> countryList = bClassDao.selectCountryList();
        List<JdGlobalGeo> countries = jdGlobalGeoDao.selectCountryList();
        System.out.println("国家个数" + countryList.size() + " vs " + countries.size() + " diff:" + (countries.size() - countryList.size()));

        // 1. 比较省份
        // 数量、省份分布、匹配百分比
        Map<String, String> countryIdCodeMap = countryList.stream().collect(Collectors.toMap(BClass::getId, BClass::getBy3));
        List<GeoProvince> provinceList = geoProvinceDao.selectProvinceList();
        List<JdGlobalGeo> provinces = jdGlobalGeoDao.selectProvinceList();
        System.out.println("省个数" + provinceList.size() + " vs " + provinces.size() + " diff:" + (provinces.size() - provinceList.size()));

        List<String> nationIdList = provinceList.stream().map(GeoProvince::getNation).toList();
        List<Map.Entry<String, String>> entryList = countryIdCodeMap.entrySet().stream().filter(e -> nationIdList.contains(e.getKey())).toList();
        System.out.println("省数据分布在" + entryList.size() + "个国家内, 分别是" + entryList.stream().map(Map.Entry::getValue).toList());
        Map<String, List<GeoProvince>> countryProvince = provinceList.stream().collect(Collectors.groupingBy(GeoProvince::getNation));
        //countryProvince.forEach((nation, datas)-> System.out.println(countryIdCodeMap.get(nation) + "省("+ nation +")数据有" + datas.size() + "个"));

        Map<String, List<JdGlobalGeo>> countryProvinceFull = provinces.stream().collect(Collectors.groupingBy(JdGlobalGeo::getParentGeoCode));
        int percentTotal = 0;
        for (Map.Entry<String, List<GeoProvince>> entry : countryProvince.entrySet()) {
            String nation = entry.getKey();
            String countryCode = countryIdCodeMap.get(nation);
            List<GeoProvince> geoProvinceList = entry.getValue();
            List<JdGlobalGeo> globalGeoList = countryProvinceFull.get(countryCode);
            List<String> provinceNameList = geoProvinceList.stream().map(GeoProvince::getProvinceName).collect(Collectors.toList());
            List<String> provinceNames = globalGeoList.stream().map(JdGlobalGeo::getName).toList();
            provinceNameList.retainAll(provinceNames);
            int percent = (int) ((double) provinceNameList.size() / geoProvinceList.size() * 100);
            percentTotal = percentTotal + percent;
            System.out.println(countryCode + "省(" + nation + ")数据有" + geoProvinceList.size() + "个; 交集; " + provinceNameList.size() + "匹配率：" + percent);
        }
        System.out.println("省数据匹配率" + percentTotal / countryProvince.size());

        //3. 城市比较
        List<BCity> cityList = bCityDao.selectCityList();
        List<JdGlobalGeo> cities = jdGlobalGeoDao.selectCityList();
        System.out.println("城市个数" + cityList.size() + " vs " + cities.size() + " diff:" + (cities.size() - cityList.size()));

        List<String> nationIdList2 = cityList.stream().map(BCity::getNation).toList();
        List<Map.Entry<String, String>> entryList2 = countryIdCodeMap.entrySet().stream().filter(e -> nationIdList2.contains(e.getKey())).toList();
        System.out.println("城市数据分布在" + entryList2.size() + "个国家内, 分别是" + entryList2.stream().map(Map.Entry::getValue).toList());

        Map<String, List<BCity>> countryCity = cityList.stream().collect(Collectors.groupingBy(BCity::getNation));
        // /1/21/102/
        Map<String, List<JdGlobalGeo>> countryCityFull = cities.stream().collect(Collectors.groupingBy(e -> e.getLevelTree().substring(5, 10)));
        Map<String, Long> codeIdMap = countries.stream().collect(Collectors.toMap(JdGlobalGeo::getGeoCode, JdGlobalGeo::getId));
        int oldCity = 0;
        int findCity = 0;
        for (Map.Entry<String, List<BCity>> entry : countryCity.entrySet()) {
            String nation = entry.getKey();
            if ("".equals(nation) || "0".equals(nation)) {
                continue;
            }
            String countryCode = countryIdCodeMap.get(nation);
            List<BCity> bCityList = entry.getValue();
            Long countryId = codeIdMap.get(countryCode);
            List<JdGlobalGeo> globalGeoList = countryCityFull.get("/" + countryId + "/");
            if (CollectionUtils.isEmpty(globalGeoList)) {
                System.out.println(nation + "国家码: " + countryCode + ";id: " + countryId + "找不到");
                continue;
            }
            List<String> cityNameList = bCityList.stream().map(BCity::getCityName).collect(Collectors.toList());
            List<String> cityNames = globalGeoList.stream().map(JdGlobalGeo::getName).toList();
            cityNameList.retainAll(cityNames);
            oldCity = oldCity + bCityList.size();
            findCity = findCity + cityNameList.size();
            System.out.println(countryCode + "国家(" + nation + ")数据有" + bCityList.size() + "个; 交集; " + cityNameList.size());
        }
        System.out.println("城市数据匹配率" + (double) findCity / oldCity * 100);
    }

    /**
     * 处理国家数据
     * 旧数据重复的国家二字码('AG','AS','GB','PF','PW', 'ES')
     */
    public void handlerCountry() {
        List<BClass> countryList = bClassDao.selectCountryList();
        Map<String, BClass> countryCodeMap = countryList.stream().collect(Collectors.toMap(BClass::getBy3, Function.identity()));

        List<JdGlobalGeo> countries = jdGlobalGeoDao.selectCountryList();
        Map<String, JdGlobalGeo> geoCodeMap = countries.stream().collect(Collectors.toMap(JdGlobalGeo::getGeoCode, Function.identity()));

        /*List<String> nations = Lists.newArrayList();
        for (BClass bClass : countryList) {
            String by3 = bClass.getBy3();
            JdGlobalGeo jdGlobalGeo = geoCodeMap.get(by3);

            boolean diff = jdGlobalGeo.getName().equals(bClass.getCName());
            if (!diff) {
                nations.add(bClass.getId());
                System.out.println(bClass.getId() + " " + by3 + "国家名：" + bClass.getCName() +  " vs " + jdGlobalGeo.getName() + ";英文名 " + bClass.getEngName() + " vs " + jdGlobalGeo.getNameEn());
            }
        }
        System.out.println(nations);*/

        List<BClass> insertList = Lists.newArrayList();
        List<BClass> updateList = Lists.newArrayList();
        Date date = new Date();
        for (Map.Entry<String, JdGlobalGeo> entry : geoCodeMap.entrySet()) {
            String geoCode = entry.getKey();
            JdGlobalGeo jdGlobalGeo = entry.getValue();
            BClass bClass = countryCodeMap.get(geoCode);
            if (bClass == null) {
                bClass = new BClass();
                bClass.setCName(jdGlobalGeo.getName());
                bClass.setBy1(PinyinUtil.getPinYinHeadChar(bClass.getCName()).toUpperCase());
                bClass.setEngName(jdGlobalGeo.getNameEn());
                bClass.setSn(28);
                bClass.setParNo("101");
                bClass.setTypeNo("NATION");
                bClass.setBy3(geoCode);
                bClass.setCreatedate(date);
                insertList.add(bClass);
            } else {
                bClass.setCName(jdGlobalGeo.getName());
                bClass.setEngName(jdGlobalGeo.getNameEn());
                bClass.setBy1(PinyinUtil.getPinYinHeadChar(bClass.getCName()).toUpperCase());
                bClass.setSavedate(date);
                updateList.add(bClass);
            }
        }
        int start = 6256;
        for (int i = 0; i < insertList.size(); i++) {
            BClass bClass = insertList.get(i);
            bClass.setId(String.format("%0" + 7 + "d", start + i));
        }
        bClassDao.saveBatch(insertList);
        for (BClass bClass : updateList) {
            bClassDao.update(bClass);
        }
    }

    /**
     * 处理省份数据
     */
    public void handlerProvince() {

        List<String> list = Lists.newArrayList("UY", "BR", "CA", "RU", "US", "GB", "ES", "GR", "DE", "FR", "VN", "ID", "IT", "MY", "JP", "TR", "AT", "PH", "KR", "NZ", "AU");
        // 1. 查询已有的省份的国家数据
        List<BClass> countryList = bClassDao.selectDuplicateList();
        List<String> nationIdList = countryList.stream().map(BClass::getId).collect(Collectors.toList());
        List<String> geoCodeList = countryList.stream().map(BClass::getBy3).collect(Collectors.toList());
        list.retainAll(geoCodeList);
        System.out.println("国家二字码集合：" + geoCodeList);
        System.out.println("交集数据长度" + list.size() + "；具体数据：" + list);
        Map<String, String> countryIdCodeMap = countryList.stream().collect(Collectors.toMap(BClass::getId, BClass::getBy3));
        Map<String, String> countryIdCodeReverseMap = countryIdCodeMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Map<String, String> countryIdNameMap = countryList.stream().collect(Collectors.toMap(BClass::getId, BClass::getCName));


        // 2.查询出对应的省份数据
        List<GeoProvince> provinceList = geoProvinceDao.selectDuplicateList(nationIdList);
        System.out.println("省份数据长度：" + provinceList.size());
        List<Integer> ids = provinceList.stream().map(GeoProvince::getId).toList();
        System.out.println("id集合" + ids);
        geoProvinceDao.deleteByIds(ids);

        // 3. 查询出geoCodeList对应的新省份数据
        List<JdGlobalGeo> provinces = jdGlobalGeoDao.selectProvinceListByCountryCodes(geoCodeList);
        Map<String, List<JdGlobalGeo>> collect = provinces.stream().collect(Collectors.groupingBy(JdGlobalGeo::getParentGeoCode));
        List<String> checkGeoCodeList = new ArrayList<>(collect.keySet());
        System.out.println("新数据省份所属国家：" + checkGeoCodeList);
        checkGeoCodeList.retainAll(geoCodeList);
        System.out.println("交集数据长度" + checkGeoCodeList.size() + "；具体数据：" + checkGeoCodeList);
        //System.out.println("国家二字码集合：" + geoCodeList);
        List<GeoProvince> insertList = Lists.newArrayListWithCapacity(provinces.size());
        Date date = new Date();
        for (JdGlobalGeo jdGlobalGeo : provinces) {
            GeoProvince geoProvince = new GeoProvince();
            geoProvince.setProvinceName(jdGlobalGeo.getName());
            geoProvince.setProvinceFullName(jdGlobalGeo.getFullName());
            geoProvince.setProvinceEname(jdGlobalGeo.getNameEn());
            geoProvince.setNation(countryIdCodeReverseMap.get(jdGlobalGeo.getParentGeoCode()));
            geoProvince.setNationName(countryIdNameMap.get(geoProvince.getNation()));
            geoProvince.setCreatedate(date);
            insertList.add(geoProvince);
        }
        saveBatch(insertList);

        /*// 1.查询国家id和Code对应集合
        bClassDao.selectCountryList();
        Map<String, String> countryIdNameMap = countryList.stream().collect(Collectors.toMap(BClass::getId, BClass::getCName));
        Map<String, String> countryIdCodeMap = countryList.stream().collect(Collectors.toMap(BClass::getId, BClass::getBy3));
        Map<String, String> countryIdCodeReverseMap = countryIdCodeMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        // 2.查询省份数据
        List<GeoProvince> provinceList = geoProvinceDao.selectProvinceList();
        List<String> nationIdList = provinceList.stream().map(GeoProvince::getNation).toList();
        List<Map.Entry<String, String>> entryList = countryIdCodeMap.entrySet().stream().filter(e -> nationIdList.contains(e.getKey())).toList();
        List<String> alreadyExistCode = entryList.stream().map(Map.Entry::getValue).toList();
        System.out.println("当前省份分布的国家集合：" + alreadyExistCode);*/


        // 直接插入alreadyExistCode以外的国家的省份
        /*List<JdGlobalGeo> provinces = jdGlobalGeoDao.selectProvinceList();
        List<JdGlobalGeo> notExistList = provinces.stream().filter(e -> !alreadyExistCode.contains(e.getParentGeoCode())).toList();
        List<GeoProvince> insertList = Lists.newArrayListWithCapacity(notExistList.size());
        Date date = new Date();
        for (JdGlobalGeo jdGlobalGeo : notExistList) {
            GeoProvince geoProvince = new GeoProvince();
            geoProvince.setProvinceName(jdGlobalGeo.getName());
            geoProvince.setProvinceFullName(jdGlobalGeo.getFullName());
            geoProvince.setProvinceEname(jdGlobalGeo.getNameEn());
            geoProvince.setNation(countryIdCodeReverseMap.get(jdGlobalGeo.getParentGeoCode()));
            geoProvince.setNationName(countryIdNameMap.get(geoProvince.getNation()));
            geoProvince.setCreatedate(date);
            insertList.add(geoProvince);
        }
        saveBatch(insertList);*/
    }

    private void saveBatch(List<GeoProvince> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<GeoProvince> list = insertList.subList(start, j);
                geoProvinceDao.saveBatch(list);
                start = j;
            }
        }
        List<GeoProvince> provinceList = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(provinceList)) {
            geoProvinceDao.saveBatch(provinceList);
        }

    }


    public void handlerSingleProvince(String countryCode) {
        List<GeoProvince> provinceList = geoProvinceDao.selectProvinceList();
        List<String> nameList = provinceList.stream().map(GeoProvince::getProvinceName).collect(Collectors.toList());
        List<JdGlobalGeo> provinces = jdGlobalGeoDao.selectProvinceList();
        List<String> names = provinces.stream().map(JdGlobalGeo::getName).collect(Collectors.toList());

        List<BClass> countryList = bClassDao.selectDuplicateList();
        Map<String, String> countryIdCodeMap = countryList.stream().collect(Collectors.toMap(BClass::getId, BClass::getBy3));
        Map<String, String> countryIdCodeReverseMap = countryIdCodeMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Map<String, String> countryIdNameMap = countryList.stream().collect(Collectors.toMap(BClass::getId, BClass::getCName));

        /*nameList.retainAll(names);
        System.out.println("交集长度" + nameList.size());*/
        names.removeAll(nameList);
        System.out.println("多出的" + names.size() + "具体内容" + names);
        List<JdGlobalGeo> insertList1 = provinces.stream().filter(e -> names.contains(e.getName())).collect(Collectors.toList());
        System.out.println("插入长度和数据：" + insertList1.size());
        insertList1.forEach(System.out::println);
        List<GeoProvince> insertList = Lists.newArrayListWithCapacity(insertList1.size());
        Date date = new Date();
        for (JdGlobalGeo jdGlobalGeo : insertList1) {
            GeoProvince geoProvince = new GeoProvince();
            geoProvince.setProvinceName(jdGlobalGeo.getName());
            geoProvince.setProvinceFullName(jdGlobalGeo.getFullName());
            geoProvince.setProvinceEname(jdGlobalGeo.getNameEn());
            geoProvince.setNation(countryIdCodeReverseMap.get(jdGlobalGeo.getParentGeoCode()));
            geoProvince.setNationName(countryIdNameMap.get(geoProvince.getNation()));
            geoProvince.setCreatedate(date);
            insertList.add(geoProvince);
        }
        saveBatch(insertList);
    }

    public void compareAirportData() {
        List<BCityAirport> cityAirportList = bCityAirportDao.selectAll();
        System.out.println("机场总记录" + cityAirportList.size());
        List<BCityAirport> internationalList = cityAirportList.stream().filter(e -> e.getInternational() == 0).collect(Collectors.toList());
        System.out.println("国际记录(包含港澳台湾)" + internationalList.size());
        List<BCityAirport> hasCityInternationalList = internationalList.stream().filter(e -> StringUtils.hasLength(e.getCityId())).collect(Collectors.toList());
        System.out.println("已有城市id记录(包含港澳台湾)" + hasCityInternationalList.size());
        hasCityInternationalList.removeIf(e->e.getCityId().equals("DTL"));
        System.out.println("非法city_id记录1条");
        List<BCityAirport> realHasCityInternationalList = hasCityInternationalList.stream().filter(e -> StringUtils.hasLength(e.getCityName())).collect(Collectors.toList());
        System.out.println("对应的city_id有cityName的记录(包含港澳台湾)" + realHasCityInternationalList.size());
        List<BCityAirport> finalList = realHasCityInternationalList.stream().filter(e -> StringUtils.hasLength(e.getNation())).collect(Collectors.toList());
        System.out.println("本地最总有效的记录(包含港澳台湾)" + finalList.size());
        List<String> cityNameList = finalList.stream().map(BCityAirport::getCityName).collect(Collectors.toList());

        List<JdGlobalGeo> cities = jdGlobalGeoDao.selectCityList();
        List<String> cityNames = cities.stream().map(JdGlobalGeo::getName).collect(Collectors.toList());
        cityNameList.retainAll(cityNames);
        System.out.println("城市名称交集：" + cityNameList.size());

    }

    public void deleteCity() {
        // 查询出international = 0的城市
        List<BCity> cityList = bCityDao.selectCityList();
        List<BCity> internationalList = cityList.stream().filter(e -> !CHINA_LIST.contains(e.getNation())).toList();
        System.out.println("国际城市数量" + internationalList.size());
        // 4. 删除b_city中国际城市
       // batchDelete(internationalList.stream().map(BCity::getId).collect(Collectors.toList()));
    }

    private void batchDelete(List<String> internationalList) {
        int start = 0;
        for (int j = 0; j < internationalList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = internationalList.subList(start, j);
                bCityDao.deleteByIds(list);
                start = j;
            }
        }
        List<String> list = internationalList.subList(start, internationalList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            bCityDao.deleteByIds(list);
        }
    }

    public void handlerCity() {
        // 1. 查询出所有国家数据，并排除国内数据
        List<BClass> countryList = bClassDao.selectCountryList();
        countryList = countryList.stream().filter(e->!CHINA_LIST.contains(e.getId())).collect(Collectors.toList());
        // 2. 查询1的国家对应的所有省份
        List<String> nationIdList = countryList.stream().map(BClass::getId).collect(Collectors.toList());
        List<GeoProvince> provinceList = geoProvinceDao.selectProvinceListByCountryCodes(nationIdList);
        Map<String, List<GeoProvince>> nationIdGroupMap = provinceList.stream().collect(Collectors.groupingBy(GeoProvince::getNation));

        // 查询出所有国家数据
        List<JdGlobalGeo> jdGlobalGeos = jdGlobalGeoDao.selectCountryList();
        Map<String, JdGlobalGeo> countryCodeMap = jdGlobalGeos.stream().collect(Collectors.toMap(JdGlobalGeo::getGeoCode, Function.identity()));

        StringBuilder out = new StringBuilder();
        Date date = new Date();
        Long start = 100000L;
        // 改国家大约有这些城市数据需要插入
        List<BCity> insertList = Lists.newArrayListWithCapacity(650000);
        // WARN: TODO 部分国际国家下面可能只有city,可能有city和province
        // 一次性仅插入1个国家的城市数据
        for (int i = 0; i < countryList.size(); i++) {
            BClass bClass = countryList.get(i);
            System.out.println("第" + i + "个国家");
            String countryCode = bClass.getBy3();
            String nationId = bClass.getId();
            List<GeoProvince> countryProvinceList = nationIdGroupMap.get(nationId);
            JdGlobalGeo jdGlobalGeo = countryCodeMap.get(countryCode);
            String levelTree = jdGlobalGeo.getLevelTree();
            String zfl = ZZGX_MAP.get(levelTree.substring(0, 3));
            List<JdGlobalGeo> countryDatas = jdGlobalGeoDao.selectCountryListByLevelTree(levelTree);
            if (countryProvinceList == null) {
                // 部分国际没有省，直接查询国际下面的city
                countryDatas = countryDatas.stream().filter(e->e.getLevel().equals(6L)).toList();
                for (JdGlobalGeo countryData : countryDatas) {
                    BCity bCity = new BCity();
                    bCity.setId(String.valueOf(start));
                    bCity.setCityName(countryData.getName());
                    bCity.setEName(countryData.getNameEn());
                    bCity.setZfl(zfl);
                    bCity.setInternational(0);
                    bCity.setCityType(0);
                    bCity.setIsAirport(0);
                    bCity.setIsTrainStation(0);
                    bCity.setNation(nationId);
                    bCity.setProvince(null);
                    bCity.setIntroduce(countryData.getMark());
                    bCity.setElongId(String.valueOf(countryData.getId()));
                    bCity.setPyjsm(PinyinUtil.getPinYinHeadChar(bCity.getCityName()).toUpperCase());
                    bCity.setFullspell(PinyinUtil.converterToSpell(bCity.getCityName()).toUpperCase());
                    bCity.setCreatedate(date);
                    bCity.setRemark("20240511数据导入");
                    insertList.add(bCity);
                    start++;
                }
                out.append("插入国家: ").append(countryCode).append( "-").append(countryDatas.size()).append("条数据\n");
                continue;
            }
            Map<String, GeoProvince> provinceNameMap = countryProvinceList.stream().collect(Collectors.toMap(GeoProvince::getProvinceName, Function.identity()));
            // 改国家下所有省份数据
            Map<String, JdGlobalGeo> provinceLevelTreeMap = countryDatas.stream().filter(e->e.getLevel().equals(5L)).collect(Collectors.toMap(JdGlobalGeo::getLevelTree, Function.identity()));
            Set<String> provinceLevelTreeMaps = provinceLevelTreeMap.keySet();
            // 改国家下所有城市数据
            List<JdGlobalGeo> countryCities = countryDatas.stream().filter(e->e.getLevel().equals(6L)).toList();

            for (JdGlobalGeo city : countryCities) {
                BCity bCity = new BCity();
                bCity.setId(String.valueOf(start));
                bCity.setCityName(city.getName());
                bCity.setEName(city.getNameEn());
                bCity.setZfl(zfl);
                bCity.setInternational(0);
                bCity.setCityType(0);
                bCity.setIsAirport(0);
                bCity.setIsTrainStation(0);
                bCity.setNation(nationId);
                String cityLevelTree = city.getLevelTree();
                provinceLevelTreeMaps.forEach(e->{
                    if (cityLevelTree.startsWith(e)) {
                        JdGlobalGeo province = provinceLevelTreeMap.get(e);
                        String name = province.getName();
                        bCity.setProvince(String.valueOf(provinceNameMap.get(name).getId()));
                    }
                });
                bCity.setIntroduce(city.getMark());
                bCity.setElongId(String.valueOf(city.getId()));
                bCity.setPyjsm(PinyinUtil.getPinYinHeadChar(bCity.getCityName()).toUpperCase());
                bCity.setFullspell(PinyinUtil.converterToSpell(bCity.getCityName()).toUpperCase());
                bCity.setCreatedate(date);
                bCity.setRemark("20240511数据导入");
                insertList.add(bCity);
                start++;
            }
            out.append("插入国家: ").append(countryCode).append( "-").append(countryCities.size()).append("条数据\n");
        }
        System.out.println(out);
        System.out.println("总共插入" + countryList.size() + "个国家，标准数据：" + insertList.size() + "条");
        saveBatch2(insertList);
    }

    private void saveBatch2(List<BCity> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<BCity> list = insertList.subList(start, j);
                bCityDao.saveBatch(list);
                start = j;
                System.out.println(start + "插入了1000条数据");
            }

        }
        List<BCity> provinceList = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(provinceList)) {
            bCityDao.saveBatch(provinceList);
        }
    }
}
