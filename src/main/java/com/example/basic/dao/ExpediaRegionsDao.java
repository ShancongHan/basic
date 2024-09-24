package com.example.basic.dao;

import com.example.basic.entity.ExpediaRegions;

import java.util.List;

/**
 * @author han
 * @date 2024/9/23
 */
public interface ExpediaRegionsDao {

    void saveBatch(List<ExpediaRegions> insertList);

    List<ExpediaRegions> selectRegions();

    void update(ExpediaRegions expediaRegions);
}
