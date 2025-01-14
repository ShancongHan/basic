package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.dao.*;
import com.example.basic.domain.eps.Ancestors;
import com.example.basic.domain.eps.ProvinceResult;
import com.example.basic.domain.expedia.CityMatchResult;
import com.example.basic.entity.*;
import com.example.basic.helper.MappingCityHelper;
import com.example.basic.helper.MappingProvinceHelper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/12/11
 */
@Slf4j
@Service
public class GlobalHotelService {

    @Resource
    private ExpediaCountryDao expediaCountryDao;

    @Resource
    private ExpediaRegionDao expediaRegionDao;
    @Resource
    private ExpediaRegionsDao expediaRegionsDao;

    @Resource
    private OaChengxiCountryDao oaChengxiCountryDao;

    @Resource
    private WstHotelGlobalCountryDao wstHotelGlobalCountryDao;
    @Resource
    private WstHotelGlobalProvinceDao wstHotelGlobalProvinceDao;

    @Resource
    private XcCityDao xcCityDao;

    @Resource
    private XcProvinceDao xcProvinceDao;

    @Resource
    private WstHotelGlobalCityDao wstHotelGlobalCityDao;

    private static final Integer CORE_POOL_SIZE = 200;
    private static final Integer MAXIMUM_POOL_SIZE = 250;

    private static final Executor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(30000));

    public void analyzeCountry() {
        List<WstHotelGlobalCountry> wstHotelGlobalCountries = wstHotelGlobalCountryDao.selectAll();
        Map<String, WstHotelGlobalCountry> codeMap = wstHotelGlobalCountries.stream().collect(Collectors.toMap(WstHotelGlobalCountry::getCode, e -> e));

        List<ExpediaCountry> expediaCountries = expediaCountryDao.selectAll();
        Map<String, ExpediaCountry> epsCodeMap = expediaCountries.stream().collect(Collectors.toMap(ExpediaCountry::getCode, e -> e));

        for (Map.Entry<String, WstHotelGlobalCountry> entry : codeMap.entrySet()) {
            ExpediaCountry expediaCountry = epsCodeMap.get(entry.getKey());
            if (expediaCountry != null) {
                WstHotelGlobalCountry value = entry.getValue();
                value.setEpsCode(expediaCountry.getCode());
                value.setEpsName(expediaCountry.getName());
                value.setEpsNameEn(expediaCountry.getNameEn());
                wstHotelGlobalCountryDao.update(value);
            }
        }
    }

    public void analyzeProvince() throws Exception {
        List<XcProvince> xcProvinces = xcProvinceDao.selectUsaList();
        List<ExpediaRegion> expediaRegions = expediaRegionDao.selectUsaList();
        List<ProvinceResult> results = Lists.newArrayListWithCapacity(xcProvinces.size());
        List<String> notFoundList = Lists.newArrayListWithCapacity(xcProvinces.size());
        for (XcProvince xcProvince : xcProvinces) {
            ProvinceResult result = matchOneProvince(xcProvince, expediaRegions);
            if (result != null) {
                results.add(result);
            } else {
                notFoundList.add(xcProvince.getProvinceName());
            }
        }
        System.out.println(JSON.toJSONString(results));
        System.out.println(JSON.toJSONString(notFoundList));
    }

    private ProvinceResult matchOneProvince(XcProvince xcProvince, List<ExpediaRegion> expediaRegions) throws ExecutionException, InterruptedException {
        List<CompletableFuture<ProvinceResult>> futures = Lists.newArrayListWithCapacity(expediaRegions.size());
        for (ExpediaRegion expediaRegion : expediaRegions) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                boolean match = MappingProvinceHelper.match(xcProvince.getProvinceEnName(), expediaRegion.getNameEn(), false);
                if (match) {
                    ProvinceResult result = new ProvinceResult();
                    result.setProvinceId(xcProvince.getProvinceId());
                    result.setProvinceName(xcProvince.getProvinceName());
                    result.setProvinceEnName(xcProvince.getProvinceEnName());
                    result.setEpsProvinceName(expediaRegion.getName());
                    result.setEpsProvinceEnName(expediaRegion.getNameEn());
                    return result;
                }
                return null;
            }, executor));
        }
        for (CompletableFuture<ProvinceResult> future : futures) {
            ProvinceResult result = future.get();
            if (result != null) return result;
        }
        return null;
    }

    public void matchProvince() throws Exception {
        List<WstHotelGlobalProvince> wstHotelGlobalProvinces = wstHotelGlobalProvinceDao.selectAll();
        List<ExpediaRegion> expediaRegions = expediaRegionsDao.selectAllProvinces();

        Map<String, List<ExpediaRegion>> expediaCountryCodeMap = expediaRegions.stream().collect(Collectors.groupingBy(ExpediaRegion::getCountryCode));
        Map<String, List<WstHotelGlobalProvince>> countryCodeMap = wstHotelGlobalProvinces.stream().collect(Collectors.groupingBy(WstHotelGlobalProvince::getCountryCode));
        for (Map.Entry<String, List<WstHotelGlobalProvince>> entry : countryCodeMap.entrySet()) {
            String countryCode = entry.getKey();
            List<ExpediaRegion> subExpediaRegions = expediaCountryCodeMap.get(countryCode);
            if (CollectionUtils.isEmpty(subExpediaRegions)) {
                log.info("携程国家{}，在expedia数据中找不到", countryCode);
                continue;
            }
            List<WstHotelGlobalProvince> provinces = entry.getValue();
            List<ProvinceResult> results = Lists.newArrayListWithCapacity(provinces.size());
            List<String> notFoundList = Lists.newArrayListWithCapacity(provinces.size());
            for (WstHotelGlobalProvince province : provinces) {
                ProvinceResult result = matchOneProvince(province, subExpediaRegions);
                if (result != null) {
                    results.add(result);
                } else {
                    notFoundList.add(province.getName());
                }
            }
            if (!CollectionUtils.isEmpty(notFoundList)) {
                log.info("携程国家{}，有省份{}条，expedia有省份{}条, 相同的{}条，找不到省份{}条，分别是{}", countryCode, provinces.size(),
                        subExpediaRegions.size(), results.size(), notFoundList.size(), notFoundList);
            }
            for (ProvinceResult result : results) {
                WstHotelGlobalProvince province = new WstHotelGlobalProvince();
                province.setId(result.getProvinceId());
                province.setEpsRegionId(result.getEpsRegionId());
                province.setEpsName(result.getEpsProvinceName());
                province.setEpsNameEn(result.getEpsProvinceEnName());
                province.setEpsCountryCode(countryCode);
                wstHotelGlobalProvinceDao.update(province);
            }
        }
    }

    private ProvinceResult matchOneProvince(WstHotelGlobalProvince province, List<ExpediaRegion> expediaRegions) throws ExecutionException, InterruptedException {
        List<CompletableFuture<ProvinceResult>> futures = Lists.newArrayListWithCapacity(expediaRegions.size());
        for (ExpediaRegion expediaRegion : expediaRegions) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                boolean match = MappingProvinceHelper.match(province.getNameEn(), expediaRegion.getNameEn(), false);
                if (match) {
                    ProvinceResult result = new ProvinceResult();
                    result.setProvinceId(province.getId());
                    result.setProvinceName(province.getName());
                    result.setProvinceEnName(province.getNameEn());
                    result.setEpsRegionId(expediaRegion.getRegionId());
                    result.setEpsProvinceName(expediaRegion.getName());
                    result.setEpsProvinceEnName(expediaRegion.getNameEn());
                    return result;
                }
                return null;
            }, executor));
        }
        for (CompletableFuture<ProvinceResult> future : futures) {
            ProvinceResult result = future.get();
            if (result != null) return result;
        }
        return null;
    }

    public void matchOneProvince() throws Exception {
        //String countryCode = "EE";
        List<WstHotelGlobalProvince> wstHotelGlobalProvinces = wstHotelGlobalProvinceDao.selectNeedMatch();
        List<ExpediaRegion> expediaRegions = expediaRegionsDao.selectAllProvinces();
        Map<String, List<ExpediaRegion>> expediaCountryCodeMap = expediaRegions.stream().collect(Collectors.groupingBy(ExpediaRegion::getCountryCode));

        List<ProvinceResult> results = Lists.newArrayListWithCapacity(wstHotelGlobalProvinces.size());
        for (WstHotelGlobalProvince province : wstHotelGlobalProvinces) {
            String countryCode = province.getCountryCode();
            if (!expediaCountryCodeMap.containsKey(countryCode)) continue;
            ProvinceResult result = matchOneProvinceByName(province, expediaRegions);
            if (result != null) {
                results.add(result);
            }
        }
        for (ProvinceResult result : results) {
            WstHotelGlobalProvince province = new WstHotelGlobalProvince();
            province.setId(result.getProvinceId());
            province.setEpsRegionId(result.getEpsRegionId());
            province.setEpsName(result.getEpsProvinceName());
            province.setEpsNameEn(result.getEpsProvinceEnName());
            province.setEpsCountryCode(result.getEpsCountryCode());
            wstHotelGlobalProvinceDao.update(province);
        }
    }

    private ProvinceResult matchOneProvinceByName(WstHotelGlobalProvince province, List<ExpediaRegion> expediaRegions) throws ExecutionException, InterruptedException {
        List<CompletableFuture<ProvinceResult>> futures = Lists.newArrayListWithCapacity(expediaRegions.size());
        for (ExpediaRegion expediaRegion : expediaRegions) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                boolean match = MappingProvinceHelper.match(province.getName(), expediaRegion.getName(), true);
                if (match) {
                    ProvinceResult result = new ProvinceResult();
                    result.setProvinceId(province.getId());
                    result.setProvinceName(province.getName());
                    result.setProvinceEnName(province.getNameEn());
                    result.setEpsRegionId(expediaRegion.getRegionId());
                    result.setEpsProvinceName(expediaRegion.getName());
                    result.setEpsCountryCode(expediaRegion.getCountryCode());
                    return result;
                }
                return null;
            }, executor));
        }
        for (CompletableFuture<ProvinceResult> future : futures) {
            ProvinceResult result = future.get();
            if (result != null) return result;
        }
        return null;
    }

    public void analyzeCity() {
        List<ExpediaRegion> expediaRegions = expediaRegionDao.selectListByCountry("US");
        /*ExpediaRegion country = expediaRegions.stream().filter(e -> "country".equals(e.getType())).findFirst().get();
        String parentPath = country.getParentPath();
        String provincePath = parentPath + country.getRegionId() + "/";
        String provinceSign = "administrative:province";
        String provinceSign1 = "subProvince1";
        String provinceSign2 = "subProvince2";
        String provinceSign3 = "subProvince3";
        String provinceSign4 = "subProvince4";
        String citySign = "administrative:city";*/
        Map<String, ExpediaRegion> regionIdMap = expediaRegions.stream().collect(Collectors.toMap(ExpediaRegion::getRegionId, e -> e));
        for (ExpediaRegion expediaRegion : expediaRegions) {
            /*Long id = expediaRegion.getId();
            String regionId = String.valueOf(expediaRegion.getRegionId());
            String ancestors = expediaRegion.getAncestors();
            String type = expediaRegion.getType();*/
            String ancestors = expediaRegion.getAncestors();
            String path = handlerPath(ancestors, regionIdMap);
            if (path == null) continue;
            expediaRegion.setParentPath(path);
            String[] split = path.split("/");
            expediaRegion.setParentId(split[split.length - 1]);
            expediaRegionDao.updatePath(expediaRegion);
        }

        /*List<Long> provinceIds = expediaRegions.stream().filter(e -> provinceSign.equals(e.getCategories())).map(ExpediaRegion::getId).toList();
        expediaRegionDao.updateParentPath(provinceIds, provincePath);
        List<ExpediaRegion> expediaRegions1 = expediaRegions.stream().filter(e -> provinceSign1.equals(e.getCategories())).toList();*/


    }

    private String handlerPath(String ancestors, Map<String, ExpediaRegion> regionIdMap) {
        if (!StringUtils.hasLength(ancestors)) return null;
        List<Ancestors> ancestorsList = JSON.parseArray(ancestors, Ancestors.class);
        if (CollectionUtils.isEmpty(ancestorsList)) return null;
        StringBuilder builder = new StringBuilder("/0/");
        Optional<Ancestors> continent = ancestorsList.stream().filter(e -> "continent".equals(e.getType())).findFirst();
        continent.ifPresent(value -> builder.append(value.getId()).append("/"));
        Optional<Ancestors> country = ancestorsList.stream().filter(e -> "country".equals(e.getType())).findFirst();
        country.ifPresent(value -> builder.append(value.getId()).append("/"));
        List<Ancestors> provinceState = ancestorsList.stream().filter(e -> "province_state".equals(e.getType())).toList();
        if (!CollectionUtils.isEmpty(provinceState) && provinceState.size() > 5) {
            return null;
        }
        if (!CollectionUtils.isEmpty(provinceState)) {
            String provinceSign = "administrative:province";
            String provinceSign1 = "subProvince1";
            String provinceSign2 = "subProvince2";
            String provinceSign3 = "subProvince3";
            String provinceSign4 = "subProvince4";
            Optional<Ancestors> firstProvince = provinceState.stream().filter(e -> regionIdMap.get(e.getId()).getCategories().contains(provinceSign)).findFirst();
            firstProvince.ifPresent(e -> builder.append(e.getId()).append("/"));
            Optional<Ancestors> firstProvince1 = provinceState.stream().filter(e -> regionIdMap.get(e.getId()).getCategories().contains(provinceSign1)).findFirst();
            firstProvince1.ifPresent(e -> builder.append(e.getId()).append("/"));
            Optional<Ancestors> firstProvince2 = provinceState.stream().filter(e -> regionIdMap.get(e.getId()).getCategories().contains(provinceSign2)).findFirst();
            firstProvince2.ifPresent(e -> builder.append(e.getId()).append("/"));
            Optional<Ancestors> firstProvince3 = provinceState.stream().filter(e -> regionIdMap.get(e.getId()).getCategories().contains(provinceSign3)).findFirst();
            firstProvince3.ifPresent(e -> builder.append(e.getId()).append("/"));
            Optional<Ancestors> firstProvince4 = provinceState.stream().filter(e -> regionIdMap.get(e.getId()).getCategories().contains(provinceSign4)).findFirst();
            firstProvince4.ifPresent(e -> builder.append(e.getId()).append("/"));
        }
        Optional<Ancestors> highLevelRegion = ancestorsList.stream().filter(e -> "high_level_region".equals(e.getType())).findFirst();
        highLevelRegion.ifPresent(value -> builder.append(value.getId()).append("/"));
        Optional<Ancestors> multiCityVicinity = ancestorsList.stream().filter(e -> "multi_city_vicinity".equals(e.getType())).findFirst();
        multiCityVicinity.ifPresent(value -> builder.append(value.getId()).append("/"));
        Optional<Ancestors> city = ancestorsList.stream().filter(e -> "city".equals(e.getType())).findFirst();
        city.ifPresent(value -> builder.append(value.getId()).append("/"));
        Optional<Ancestors> neighborhood = ancestorsList.stream().filter(e -> "neighborhood".equals(e.getType())).findFirst();
        neighborhood.ifPresent(value -> builder.append(value.getId()).append("/"));
        return builder.toString();
    }


    public void matchCity() throws ExecutionException, InterruptedException {
        List<WstHotelGlobalCountry> wstHotelGlobalCountries = wstHotelGlobalCountryDao.selectAll();
        Map<String, String> countryCodeRelationShipMap = wstHotelGlobalCountries.stream()
                .filter(e -> StringUtils.hasLength(e.getCode()) && StringUtils.hasLength(e.getEpsCode()))
                .collect(Collectors.toMap(WstHotelGlobalCountry::getCode, WstHotelGlobalCountry::getEpsCode));
        List<WstHotelGlobalProvince> provinces = wstHotelGlobalProvinceDao.selectAll();
        Map<Integer, String> provinceIdRelationShipMap = provinces.stream()
                .filter(e -> e.getId() != null && StringUtils.hasLength(e.getEpsRegionId()))
                .collect(Collectors.toMap(WstHotelGlobalProvince::getId, WstHotelGlobalProvince::getEpsRegionId));
        List<WstHotelGlobalCity> wstHotelGlobalCities = wstHotelGlobalCityDao.selectAll();
        Map<String, List<WstHotelGlobalCity>> countryCodeMap = wstHotelGlobalCities.stream().collect(Collectors.groupingBy(WstHotelGlobalCity::getCountryCode));
        for (Map.Entry<String, List<WstHotelGlobalCity>> entry : countryCodeMap.entrySet()) {
            StopWatch watch = new StopWatch();
            watch.start("查询eps城市");
            String countryCode = entry.getKey();
            String epsCountryCode = countryCodeRelationShipMap.get(countryCode);
            if (!StringUtils.hasLength(epsCountryCode)) continue;
            List<WstHotelGlobalCity> globalCities = entry.getValue();
            List<ExpediaRegions> expediaRegionsList = expediaRegionsDao.selectListByCountryCode(epsCountryCode);
            watch.stop();
            // 把没有省份的先省略掉
           /* watch.start("没有省份的匹配");
            // 先查出没有省份的city
            List<WstHotelGlobalCity> notHasProvinceList = globalCities.stream().filter(e -> e.getProvinceId() == null).toList();
            if (!CollectionUtils.isEmpty(notHasProvinceList)) {
                List<ExpediaRegions> allEpsCityList = expediaRegionsList.stream().filter(e -> "city".equals(e.getType())).toList();
                List<CityMatchResult> results = findSameCity(notHasProvinceList, allEpsCityList);
                updateBatch(results);
                log.info("国家{}，无省份记录{}条, 匹配到城市{}个",countryCode, notHasProvinceList.size(), results.size());
                results.clear();
            }
            watch.stop();*/
            watch.start("有省份的比较");
            // 再查出没有省份的city,并按照省份分类
            Map<Integer, List<WstHotelGlobalCity>> provinceMap = globalCities.stream()
                    .filter(e -> e.getProvinceId() != null)
                    .collect(Collectors.groupingBy(WstHotelGlobalCity::getProvinceId));
            //noinspection OptionalGetWithoutIsPresent
            ExpediaRegions epsCountry = expediaRegionsList.stream().filter(e -> "country".equals(e.getType())).findFirst().get();
            String parentId = epsCountry.getParentId();
            String path = "/" + parentId + "/" + epsCountry.getRegionId() + "/";
            for (Map.Entry<Integer, List<WstHotelGlobalCity>> subEntry : provinceMap.entrySet()) {
                Integer provinceId = subEntry.getKey();
                List<WstHotelGlobalCity> onePrivinceCityList = subEntry.getValue();
                // 当前省份下没有城市，直接跳过
                if (CollectionUtils.isEmpty(onePrivinceCityList)) continue;
                String provinceName = onePrivinceCityList.get(0).getProvinceName();
                String epsProvinceId = provinceIdRelationShipMap.get(provinceId);
                // 如果找不到eps的对应省份，直接跳过
                if (!StringUtils.hasLength(epsProvinceId)) continue;
                String provincePath = path + epsProvinceId;
                // 严格按照省份组装path路径，筛选出城市列表
                List<ExpediaRegions> toughEpsCityList = expediaRegionsList.stream()
                        .filter(e -> ("city".equals(e.getType()) || "high_level_region".equals(e.getType()) || "neighborhood".equals(e.getType()) || "multi_city_vicinity".equals(e.getType()))
                                && (StringUtils.hasLength(e.getParentPath()) && (e.getParentPath().startsWith(provincePath)))).toList();
                List<CityMatchResult> results = Lists.newArrayListWithCapacity(onePrivinceCityList.size());
                List<CityMatchResult> matchList = findSameCity(onePrivinceCityList, toughEpsCityList);
                if (!CollectionUtils.isEmpty(matchList)) results.addAll(matchList);
                /*Set<Integer> matchCityIds = matchList.stream().map(CityMatchResult::getCityId).collect(Collectors.toSet());
                List<WstHotelGlobalCity> stillNotFoundList = onePrivinceCityList.stream().filter(e -> !matchCityIds.contains(e.getCityId())).toList();
                List<ExpediaRegions> looseEpsCityList = expediaRegionsList.stream()
                        .filter(e -> ("city".equals(e.getType()) || "high_level_region".equals(e.getType()) || "neighborhood".equals(e.getType()) || "multi_city_vicinity".equals(e.getType()))
                                && (StringUtils.hasLength(e.getParentPath()) && (e.getParentPath().startsWith(path)))).toList();*/
                updateBatch(results);
                /*for (WstHotelGlobalCity wstHotelGlobalCity : cityList) {
                    *//*Integer cityId = wstHotelGlobalCity.getCityId();
                    String name = wstHotelGlobalCity.getName();
                    String nameEn = wstHotelGlobalCity.getNameEn();
                    Map<String, List<ExpediaRegions>> nameMap = epsCityList.stream().collect(Collectors.groupingBy(ExpediaRegions::getName));
                    Map<String, List<ExpediaRegions>> nameEnMap = epsCityList.stream().collect(Collectors.groupingBy(ExpediaRegions::getNameEn));
                    if (nameMap.containsKey(name)) {
                        List<ExpediaRegions> findList = nameMap.get(name);
                        if (findList.size() == 1) {
                            ExpediaRegions expediaRegions = findList.get(0);
                            boolean nameEnMatch = MappingCityHelper.matchNameEn(nameEn, expediaRegions.getNameEn());
                            if (nameEnMatch) {
                                CityMatchResult result = setResult(cityId, name, nameEn, expediaRegions);
                                results.add(result);
                                continue;
                            }
                        }
                    }
                    if (nameEnMap.containsKey(nameEn)) {
                        List<ExpediaRegions> findList = nameEnMap.get(nameEn);
                        if (findList.size() == 1) {
                            ExpediaRegions expediaRegions = findList.get(0);
                            boolean nameMatch = MappingCityHelper.matchName(name, expediaRegions.getName());
                            if (nameMatch) {
                                CityMatchResult result = setResult(cityId, name, nameEn, expediaRegions);
                                results.add(result);
                                continue;
                            }
                        }
                    }*//*

                }*/
                log.info("国家{}，省份{}({}), 匹配到这些城市{}",countryCode, provinceName, provinceId, results.size());
            }
            watch.stop();
            log.info("国家{}, 匹配耗时{}, 分别耗时{}",countryCode, watch.getTotalTimeSeconds(), watch.prettyPrint());
        }
    }

    private void updateBatch(List<CityMatchResult> results) {
        if (!CollectionUtils.isEmpty(results)) {
            for (CityMatchResult result : results) {
                wstHotelGlobalCityDao.update(result);
            }
        }
    }

    /**
     * 2个列表互相找相同的城市，仅考虑名字
     * @param globalCities 本地城市列表
     * @param epsCityList eps城市列表
     * @return 匹配的结果
     * @throws ExecutionException 线程异常
     * @throws InterruptedException 中断异常
     */
    private List<CityMatchResult> findSameCity(List<WstHotelGlobalCity> globalCities, List<ExpediaRegions> epsCityList) throws ExecutionException, InterruptedException {
        List<CompletableFuture<CityMatchResult>> futures = Lists.newArrayListWithCapacity(globalCities.size());
        List<CityMatchResult> results = Lists.newArrayListWithCapacity(globalCities.size());
        for (WstHotelGlobalCity wstHotelGlobalCity : globalCities) {
            for (ExpediaRegions expediaRegions : epsCityList) {
                Integer cityId = wstHotelGlobalCity.getCityId();
                String name = wstHotelGlobalCity.getName();
                String nameEn = wstHotelGlobalCity.getNameEn();
                futures.add(CompletableFuture.supplyAsync(() -> {
                    boolean match = MappingCityHelper.match(name, expediaRegions.getName(), nameEn, expediaRegions.getNameEn());
                    if (match) {
                        return setResult(cityId, name, nameEn, expediaRegions);
                    }
                    return null;
                }, executor));
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        for (CompletableFuture<CityMatchResult> future : futures) {
            CityMatchResult result = future.get();
            if (result != null) results.add(result);
        }
        return results;
    }

    private CityMatchResult setResult(Integer cityId, String name, String nameEn, ExpediaRegions expediaRegions) {
        CityMatchResult result = new CityMatchResult();
        result.setCityId(cityId);
        result.setName(name);
        result.setNameEn(nameEn);
        result.setEpsCityId(expediaRegions.getRegionId());
        result.setEpsName(expediaRegions.getName());
        result.setEpsNameEn(expediaRegions.getNameEn());
        result.setEpsFullName(expediaRegions.getFullName());
        result.setEpsFullNameEn(expediaRegions.getFullNameEn());
        result.setEpsCityType(expediaRegions.getType());
        result.setEpsCountryCode(expediaRegions.getCountryCode());
        result.setCenterLongitude(expediaRegions.getCenterLongitude());
        result.setCenterLatitude(expediaRegions.getCenterLatitude());
        return result;
    }
}
