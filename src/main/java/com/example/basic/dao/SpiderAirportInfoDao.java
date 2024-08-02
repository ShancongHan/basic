package com.example.basic.dao;

import com.example.basic.entity.SpiderAirportInfo;

import java.util.List;

/**
 * @author han
 * @date 2024/7/1
 */
public interface SpiderAirportInfoDao {

    void saveBatch(List<SpiderAirportInfo> insertList);

    List<SpiderAirportInfo> selectAll();

    void update(SpiderAirportInfo info);
}
