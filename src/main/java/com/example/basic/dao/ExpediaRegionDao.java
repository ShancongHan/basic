package com.example.basic.dao;

import com.example.basic.entity.ExpediaRegion;

import java.util.List;

/**
 * @author han
 * @date 2024/7/31
 */
public interface ExpediaRegionDao {

    void saveBatch(List<ExpediaRegion> insertList);

    int update(ExpediaRegion expediaRegion);

    List<ExpediaRegion> selectAll();
}
