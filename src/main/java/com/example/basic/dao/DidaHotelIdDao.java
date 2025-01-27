package com.example.basic.dao;

import com.example.basic.entity.DidaHotelId;

import java.util.List;

/**
 * @author han
 * @date 2025/1/27
 */
public interface DidaHotelIdDao {

    void saveBatch(List<DidaHotelId> list);

    List<Integer> selectAll();
}
