package com.example.basic.dao;

import com.example.basic.entity.ExpediaAmenities;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaAmenitiesDao {
    void saveBatch(List<ExpediaAmenities> insertList);
}
