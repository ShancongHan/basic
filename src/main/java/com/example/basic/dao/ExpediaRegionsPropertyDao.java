package com.example.basic.dao;

import com.example.basic.entity.ExpediaRegionsProperty;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/9/23
 */
public interface ExpediaRegionsPropertyDao {

    void saveBatch(List<ExpediaRegionsProperty> insertList);

    Set<String> selectAllRegionIds();

    List<ExpediaRegionsProperty> selectListByRegionIds(List<String> regionIds);
}
