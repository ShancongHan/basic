package com.example.basic.dao;

import com.example.basic.domain.eps.RegionPropertyResult;
import com.example.basic.entity.ExpediaRegion;
import com.example.basic.entity.ExpediaRegions;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author han
 * @date 2024/9/23
 */
public interface ExpediaRegionsDao {

    void saveBatch(List<ExpediaRegions> insertList);

    List<ExpediaRegions> selectRegions();

    void update(ExpediaRegions expediaRegions);

    List<ExpediaRegions> selectAllAncestors();

    void updateContinent(@Param(value = "id") Long id, @Param(value = "continentId") String continentId);

    List<ExpediaRegions> selectAll();

    Integer updateSomeData(ExpediaRegions expediaRegion);

    List<ExpediaRegions> selectListByCountryCode(String countryCode);

    void saveFullBatch(List<ExpediaRegions> insertList);

    Integer updateHasZh(List<String> regionIds);

    Integer updateHasEn(List<String> regionIds);

    List<ExpediaRegions> selectAllRegionIds();

    List<ExpediaRegions> selectAllNeedStatistics();

    void updateStatistics(RegionPropertyResult expediaRegionsProperty);

    List<ExpediaRegions> selectAllCity();

    List<ExpediaRegion> selectAllProvinces();

    List<ExpediaRegion> selectAllProvincesByCountryCode(String countryCode);
}
