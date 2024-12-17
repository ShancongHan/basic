package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.dao.*;
import com.example.basic.domain.CalculateResult;
import com.example.basic.domain.eps.Ancestors;
import com.example.basic.domain.eps.ProvinceResult;
import com.example.basic.entity.*;
import com.example.basic.helper.MappingProvinceHelper;
import com.example.basic.helper.MappingScoreHelper2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private OaChengxiCountryDao oaChengxiCountryDao;

    @Resource
    private WstHotelGlobalCountryDao wstHotelGlobalCountryDao;
    @Resource
    private WstHotelGlobalProvinceDao wstHotelGlobalProvinceDao;

    @Resource
    private XcCityDao xcCityDao;

    @Resource
    private XcProvinceDao xcProvinceDao;

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
                boolean match = MappingProvinceHelper.match(xcProvince.getProvinceEnName(), expediaRegion.getNameEn());
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
        List<ExpediaRegion> expediaRegions = expediaRegionDao.selectAllProvinces();

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
                boolean match = MappingProvinceHelper.match(province.getNameEn(), expediaRegion.getNameEn());
                if (match) {
                    ProvinceResult result = new ProvinceResult();
                    result.setProvinceId(province.getId());
                    result.setProvinceName(province.getName());
                    result.setProvinceEnName(province.getNameEn());
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

    public void analyzeCity() {
        List<ExpediaRegion> expediaRegions = expediaRegionDao.selectListByCountry("US");
        ExpediaRegion country = expediaRegions.stream().filter(e -> "country".equals(e.getType())).findFirst().get();
        String parentPath = country.getParentPath();
        String provincePath = parentPath + country.getRegionId() + "/";
        String provinceSign = "administrative:province";
        String provinceSign1 = "subProvince1";
        String provinceSign2 = "subProvince2";
        String provinceSign3 = "subProvince3";
        String provinceSign4 = "subProvince4";
        String citySign = "administrative:city";
        Map<String, ExpediaRegion> regionIdMap = expediaRegions.stream().collect(Collectors.toMap(ExpediaRegion::getRegionId, e -> e));
        for (ExpediaRegion expediaRegion : expediaRegions) {
            Long id = expediaRegion.getId();
            String regionId = String.valueOf(expediaRegion.getRegionId());
            String categories = expediaRegion.getCategories();
            String ancestors = expediaRegion.getAncestors();
            String type = expediaRegion.getType();
            if ("province_state".equals(type)) {
                if (provinceSign.equals(categories)) {
                    expediaRegion.setParentPath(parentPath);
                }
                if (provinceSign1.equals(categories)) {
                    // [{"id":"207","type":"province_state"},{"id":"201","type":"country"},{"id":"500001","type":"continent"}]
                    String fatherRegionId = findProvinceFather(ancestors, regionIdMap, provinceSign);
                    expediaRegion.setParentPath(parentPath + "/" + fatherRegionId);
                }
                if (provinceSign2.equals(categories)) {
                    String fatherRegionId = findProvinceFather(ancestors, regionIdMap, provinceSign1);
                    expediaRegion.setParentPath(parentPath + "/" + fatherRegionId);
                }
                if (provinceSign3.equals(categories)) {
                    String fatherRegionId = findProvinceFather(ancestors, regionIdMap, provinceSign2);
                    expediaRegion.setParentPath(parentPath + "/" + fatherRegionId);
                }
                if (provinceSign4.equals(categories)) {
                    String fatherRegionId = findProvinceFather(ancestors, regionIdMap, provinceSign3);
                    expediaRegion.setParentPath(parentPath + "/" + fatherRegionId);
                }
            }
            if ("multi_city_vicinity".equals(type)) {
                if (citySign.equals(categories)) {

                }
            }
            if ("city".equals(type)) {
                if (citySign.equals(categories)) {

                }
            }
        }

        List<Long> provinceIds = expediaRegions.stream().filter(e -> provinceSign.equals(e.getCategories())).map(ExpediaRegion::getId).toList();
        expediaRegionDao.updateParentPath(provinceIds, provincePath);
        List<ExpediaRegion> expediaRegions1 = expediaRegions.stream().filter(e -> provinceSign1.equals(e.getCategories())).toList();


    }

    private String findProvinceFather(String ancestors, Map<String, ExpediaRegion> regionIdMap, String parentSign) {
        List<Ancestors> ancestorsList = JSON.parseArray(ancestors, Ancestors.class);
        List<Ancestors> provinceState = ancestorsList.stream().filter(e -> e.getType().equals("province_state")).toList();
        for (Ancestors ancestor : provinceState) {
            ExpediaRegion expediaRegion = regionIdMap.get(ancestor.getId());
            String maybeAncestors = expediaRegion.getAncestors();
            if (maybeAncestors.equals(parentSign)) {
                return expediaRegion.getRegionId();
            }
        }
        return null;
    }
}
