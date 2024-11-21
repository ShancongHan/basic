package com.example.basic.dao;

import com.example.basic.entity.WstHotelStatistics;

import java.util.List;

/**
 * @author han
 * @date 2024/11/18
 */
public interface WstHotelStatisticsDao {
    void saveBatch(List<WstHotelStatistics> list);
}
