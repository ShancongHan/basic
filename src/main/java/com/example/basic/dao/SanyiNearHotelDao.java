package com.example.basic.dao;

import com.example.basic.entity.SanyiNearHotel;

import java.util.List;

/**
 * @author han
 * @date 2024/7/10
 */
public interface SanyiNearHotelDao {

    void saveBatch(List<SanyiNearHotel> insertList);

    List<SanyiNearHotel> selectAll();
}
