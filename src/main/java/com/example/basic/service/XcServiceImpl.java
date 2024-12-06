package com.example.basic.service;

import com.example.basic.dao.*;
import com.example.basic.domain.xc.GroupResult;
import com.example.basic.entity.*;
import com.example.basic.helper.MappingGroupHelper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/12/4
 */
@Service
public class XcServiceImpl {

    @Resource
    private SysGroupDao sysGroupDao;

    @Resource
    private OaChengxiHotelGroupDao oaChengxiHotelGroupDao;

    @Resource
    private SysBrandDao sysBrandDao;
    @Resource
    private SysAreaDao sysAreaDao;

    @Resource
    private OaChengxiHotelBrandDao oaChengxiHotelBrandDao;
    @Resource
    private OaChengxiHotelZoneDao oaChengxiHotelZoneDao;
    @Resource
    private OaChengxiHotelCountyDao oaChengxiHotelCountyDao;

    @Resource
    private SysZoneDao sysZoneDao;

    @Resource
    private WstHotelCityDao wstHotelCityDao;
    @Resource
    private OaChengxiHotelCityDao oaChengxiHotelCityDao;

    private static final Executor executor = new ThreadPoolExecutor(30, 50,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(5000));

    public void matchGroup() throws ExecutionException, InterruptedException {
        List<SysGroup> sysGroups = sysGroupDao.selectAll();
        Map<String, SysGroup> nameGroupMap = sysGroups.stream().collect(Collectors.toMap(SysGroup::getGroupName, e -> e));
        Set<String> nameSet = nameGroupMap.keySet();
        List<OaChengxiHotelGroup> oaChengxiHotelGroups = oaChengxiHotelGroupDao.selectAll();
        for (OaChengxiHotelGroup oaChengxiHotelGroup : oaChengxiHotelGroups) {
            String name = oaChengxiHotelGroup.getName();
            if (nameGroupMap.containsKey(name)) {
                SysGroup sysGroup = nameGroupMap.get(name);
                oaChengxiHotelGroup.setSysGroupId(sysGroup.getGroupId());
                oaChengxiHotelGroup.setSysGroupName(sysGroup.getGroupName());
                oaChengxiHotelGroupDao.update(oaChengxiHotelGroup);
                continue;
            }
            List<CompletableFuture<GroupResult>> futures = Lists.newArrayListWithCapacity(nameSet.size());
            for (String sysName : nameSet) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    boolean compare = MappingGroupHelper.compare(name, sysName);
                    if (compare) {
                        GroupResult groupResult = new GroupResult();
                        groupResult.setGroupName(name);
                        groupResult.setSysGroupName(sysName);
                        return groupResult;
                    }
                    return null;
                }, executor));
            }
            List<GroupResult> results = Lists.newArrayListWithCapacity(nameSet.size());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            for (CompletableFuture<GroupResult> future : futures) {
                GroupResult groupResult = future.get();
                if (groupResult == null) {
                    continue;
                }
                results.add(groupResult);
            }

            if (results.size() == 1) {
                SysGroup sysGroup = nameGroupMap.get(results.get(0).getSysGroupName());
                oaChengxiHotelGroup.setSysGroupId(sysGroup.getGroupId());
                oaChengxiHotelGroup.setSysGroupName(sysGroup.getGroupName());
                oaChengxiHotelGroupDao.update(oaChengxiHotelGroup);
            } else {
                System.out.println("找不到" + name);
            }
        }

    }

    public void matchBrand() throws ExecutionException, InterruptedException {
        List<SysBrand> sysBrands = sysBrandDao.selectAll();
        Map<String, SysBrand> nameBrandMap = sysBrands.stream().collect(Collectors.toMap(SysBrand::getBrandName, e -> e));
        Set<String> nameSet = nameBrandMap.keySet();
        List<OaChengxiHotelBrand> oaChengxiHotelBrands = oaChengxiHotelBrandDao.selectAll();
        for (OaChengxiHotelBrand oaChengxiHotelBrand : oaChengxiHotelBrands) {
            String name = oaChengxiHotelBrand.getName();
            if (nameBrandMap.containsKey(name)) {
                SysBrand sysBrand = nameBrandMap.get(name);
                oaChengxiHotelBrand.setSysBrandId(sysBrand.getBrandId());
                oaChengxiHotelBrand.setSysBrandName(sysBrand.getBrandName());
                oaChengxiHotelBrandDao.update(oaChengxiHotelBrand);
                continue;
            }
            List<CompletableFuture<GroupResult>> futures = Lists.newArrayListWithCapacity(nameSet.size());
            for (String sysName : nameSet) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    boolean compare = MappingGroupHelper.compare(name, sysName);
                    if (compare) {
                        GroupResult groupResult = new GroupResult();
                        groupResult.setGroupName(name);
                        groupResult.setSysGroupName(sysName);
                        return groupResult;
                    }
                    return null;
                }, executor));
            }
            List<GroupResult> results = Lists.newArrayListWithCapacity(nameSet.size());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            for (CompletableFuture<GroupResult> future : futures) {
                GroupResult groupResult = future.get();
                if (groupResult == null) {
                    continue;
                }
                results.add(groupResult);
            }

            if (results.size() == 1) {
                SysBrand sysBrand = nameBrandMap.get(results.get(0).getSysGroupName());
                oaChengxiHotelBrand.setSysBrandId(sysBrand.getBrandId());
                oaChengxiHotelBrand.setSysBrandName(sysBrand.getBrandName());
                oaChengxiHotelBrandDao.update(oaChengxiHotelBrand);
            } else {
                System.out.println("找不到" + name);
            }
        }
    }

    public void matchZone() throws ExecutionException, InterruptedException {
        List<SysZone> sysZones = sysZoneDao.selectAll();
        Map<String, List<SysZone>> cityIdMap = sysZones.stream().collect(Collectors.groupingBy(SysZone::getSysCityId));
        List<String> foundMultiList = Lists.newArrayListWithCapacity(10000);
        List<OaChengxiHotelZone> oaChengxiHotelZoneList = oaChengxiHotelZoneDao.selectAll();
        Map<String, List<OaChengxiHotelZone>> cityIdMap2 = oaChengxiHotelZoneList.stream().filter(e->e.getSysCityId() != null).collect(Collectors.groupingBy(OaChengxiHotelZone::getSysCityId));
        for (Map.Entry<String, List<OaChengxiHotelZone>> entry : cityIdMap2.entrySet()) {
            String cityId = entry.getKey();
            List<SysZone> sysZonesList = cityIdMap.get(cityId);
            if (CollectionUtils.isEmpty(sysZonesList)) {
                continue;
            }
            Map<String, SysZone> nameZoneMap = sysZonesList.stream().collect(Collectors.toMap(SysZone::getSysZoneName, e -> e));
            Set<String> nameSet = nameZoneMap.keySet();
            List<OaChengxiHotelZone> oaChengxiHotelZones = entry.getValue();
            for (OaChengxiHotelZone oaChengxiHotelZone : oaChengxiHotelZones) {
                String name = oaChengxiHotelZone.getZoneCentralName();
                if (nameZoneMap.containsKey(name)) {
                    SysZone sysZone = nameZoneMap.get(name);
                    oaChengxiHotelZone.setSysZoneId(sysZone.getSysZoneId());
                    oaChengxiHotelZone.setSysZoneName(sysZone.getSysZoneName());
                    oaChengxiHotelZoneDao.update(oaChengxiHotelZone);
                    continue;
                }
                List<CompletableFuture<GroupResult>> futures = Lists.newArrayListWithCapacity(nameSet.size());
                for (String sysName : nameSet) {
                    futures.add(CompletableFuture.supplyAsync(() -> {
                        boolean compare = MappingGroupHelper.compare2(name, sysName);
                        if (compare) {
                            GroupResult groupResult = new GroupResult();
                            groupResult.setGroupName(name);
                            groupResult.setSysGroupName(sysName);
                            return groupResult;
                        }
                        return null;
                    }, executor));
                }
                List<GroupResult> results = Lists.newArrayListWithCapacity(nameSet.size());
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                for (CompletableFuture<GroupResult> future : futures) {
                    GroupResult groupResult = future.get();
                    if (groupResult == null) {
                        continue;
                    }
                    results.add(groupResult);
                }

                if (CollectionUtils.isEmpty(results)) {
                    System.out.println("找不到" + name);
                } else {
                    if (results.size() == 1) {
                        SysZone sysZone = nameZoneMap.get(results.get(0).getSysGroupName());
                        oaChengxiHotelZone.setSysZoneId(sysZone.getSysZoneId());
                        oaChengxiHotelZone.setSysZoneName(sysZone.getSysZoneName());
                        oaChengxiHotelZoneDao.update(oaChengxiHotelZone);
                        continue;
                    }
                    foundMultiList.add(oaChengxiHotelZone.getZoneId());
                }
            }
        }
        System.out.println("这些商圈遭到多个" + String.join(",", foundMultiList));
    }

    public void matchZone111() {
        List<SysZone> sysZones = sysZoneDao.selectAll();
        Map<String, List<SysZone>> cityIdMap = sysZones.stream().collect(Collectors.groupingBy(SysZone::getSysCityId));
        List<Integer> deleteList = Lists.newArrayListWithExpectedSize(2000);
        for (Map.Entry<String, List<SysZone>> entry : cityIdMap.entrySet()) {
            List<SysZone> value = entry.getValue();
            Map<String, List<SysZone>> collect = value.stream().collect(Collectors.groupingBy(SysZone::getSysZoneName));
            for (Map.Entry<String, List<SysZone>> stringListEntry : collect.entrySet()) {
                if (stringListEntry.getValue().size() > 1) {
                    deleteList.addAll(stringListEntry.getValue().stream().map(SysZone::getId).toList());
                }
            }
        }
        //System.out.println(deleteList.size());
        sysZoneDao.deleteMulti(deleteList);
    }

    public void matchArea() throws ExecutionException, InterruptedException {
        List<SysArea> sysAreas = sysAreaDao.selectAll();
        Map<String, List<SysArea>> cityIdMap = sysAreas.stream().collect(Collectors.groupingBy(SysArea::getCityId));
        List<Integer> foundMultiList = Lists.newArrayListWithCapacity(10000);
        List<OaChengxiHotelCounty> oaChengxiHotelCounties = oaChengxiHotelCountyDao.selectChina();
        Map<String, List<OaChengxiHotelCounty>> cityIdMap2 = oaChengxiHotelCounties.stream().filter(e->e.getSysCityId() != null).collect(Collectors.groupingBy(OaChengxiHotelCounty::getSysCityId));
        for (Map.Entry<String, List<OaChengxiHotelCounty>> entry : cityIdMap2.entrySet()) {
            String cityId = entry.getKey();
            List<SysArea> sysAreaList = cityIdMap.get(cityId);
            if (CollectionUtils.isEmpty(sysAreaList)) {
                continue;
            }
            Map<String, SysArea> nameAreaMap = sysAreaList.stream().collect(Collectors.toMap(SysArea::getAreaName, e -> e));
            Set<String> nameSet = nameAreaMap.keySet();
            List<OaChengxiHotelCounty> oaChengxiHotelCountyList = entry.getValue();
            for (OaChengxiHotelCounty oaChengxiHotelCounty : oaChengxiHotelCountyList) {
                String name = oaChengxiHotelCounty.getCountyName();
                if (nameAreaMap.containsKey(name)) {
                    SysArea sysArea = nameAreaMap.get(name);
                    oaChengxiHotelCounty.setSysCountyId(sysArea.getAreaCode());
                    oaChengxiHotelCounty.setSysCountyName(sysArea.getAreaName());
                    oaChengxiHotelCountyDao.update(oaChengxiHotelCounty);
                    continue;
                }
                List<CompletableFuture<GroupResult>> futures = Lists.newArrayListWithCapacity(nameSet.size());
                for (String sysName : nameSet) {
                    futures.add(CompletableFuture.supplyAsync(() -> {
                        boolean compare = MappingGroupHelper.compare3(name, sysName);
                        if (compare) {
                            GroupResult groupResult = new GroupResult();
                            groupResult.setGroupName(name);
                            groupResult.setSysGroupName(sysName);
                            return groupResult;
                        }
                        return null;
                    }, executor));
                }
                List<GroupResult> results = Lists.newArrayListWithCapacity(nameSet.size());
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                for (CompletableFuture<GroupResult> future : futures) {
                    GroupResult groupResult = future.get();
                    if (groupResult == null) {
                        continue;
                    }
                    results.add(groupResult);
                }

                if (CollectionUtils.isEmpty(results)) {
                    System.out.println("找不到" + name);
                } else {
                    if (results.size() == 1) {
                        SysArea sysArea = nameAreaMap.get(results.get(0).getSysGroupName());
                        oaChengxiHotelCounty.setSysCountyId(sysArea.getAreaCode());
                        oaChengxiHotelCounty.setSysCountyName(sysArea.getAreaName());
                        oaChengxiHotelCountyDao.update(oaChengxiHotelCounty);
                        continue;
                    }
                    foundMultiList.add(oaChengxiHotelCounty.getCountyId());
                }
            }
        }
        System.out.println("这些地区到多个" + foundMultiList.stream().map(Objects::toString).collect(Collectors.joining(",")));
    }
}
