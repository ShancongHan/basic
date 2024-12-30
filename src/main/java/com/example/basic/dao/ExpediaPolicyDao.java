package com.example.basic.dao;

import com.example.basic.entity.ExpediaPolicy;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaPolicyDao {
    void saveBatch(List<ExpediaPolicy> insertList);

    void update(ExpediaPolicy expediaPolicy);
}
