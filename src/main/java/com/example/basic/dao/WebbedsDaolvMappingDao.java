package com.example.basic.dao;

import com.example.basic.entity.WebbedsDaolvMapping;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/5/29
 */
public interface WebbedsDaolvMappingDao {

    void saveBatch(List<WebbedsDaolvMapping> insertList);

    List<WebbedsDaolvMapping> selectAll();

    Set<Integer> selectWebbedsHotelIdList();

    Set<Integer> selectDaolvHotelIdList();
}
