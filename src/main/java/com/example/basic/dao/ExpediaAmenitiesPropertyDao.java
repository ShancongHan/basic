package com.example.basic.dao;

import com.example.basic.entity.ExpediaAmenitiesProperty;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaAmenitiesPropertyDao {

    List<ExpediaAmenitiesProperty> selectAll();

    void saveBatch(List<ExpediaAmenitiesProperty> insertList);
}
