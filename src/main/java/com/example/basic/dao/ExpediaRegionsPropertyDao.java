package com.example.basic.dao;

import com.example.basic.entity.ExpediaRegionsProperty;

import java.util.List;

/**
 * @author han
 * @date 2024/9/23
 */
public interface ExpediaRegionsPropertyDao {

    void saveBatch(List<ExpediaRegionsProperty> insertList);
}
