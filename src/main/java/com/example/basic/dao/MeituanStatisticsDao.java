package com.example.basic.dao;

import com.example.basic.entity.MeituanStatistics;

import java.util.List;

/**
 * @author han
 * @date 2024/11/18
 */
public interface MeituanStatisticsDao {
    List<MeituanStatistics> selectAll();
}
