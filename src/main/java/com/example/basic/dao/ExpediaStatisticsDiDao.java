package com.example.basic.dao;

import com.example.basic.entity.ExpediaStatistics;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaStatisticsDiDao {
    List<ExpediaStatistics> selectAll();

    void saveBatch(List<ExpediaStatistics> insertList);
}
