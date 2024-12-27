package com.example.basic.dao;


import com.example.basic.entity.ExpediaStatistics;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaStatisticsDao {

    void saveBatch(List<ExpediaStatistics> insertList);

}
