package com.example.basic.dao;

import com.example.basic.entity.ExpediaRegion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author han
 * @date 2024/7/31
 */
public interface ExpediaRegionDao {

    void saveBatch(List<ExpediaRegion> insertList);

    int update(ExpediaRegion expediaRegion);

    List<ExpediaRegion> selectAll();

    List<ExpediaRegion> selectCategories();

    List<ExpediaRegion> selectTags();

    List<ExpediaRegion> selectUsaList();

    List<ExpediaRegion> selectAllProvinces();

    List<ExpediaRegion> selectListByCountry(String countryCode);

    void updateParentPath(@Param(value = "provinceIds") List<Long> provinceIds, @Param(value = "provincePath") String provincePath);
}
