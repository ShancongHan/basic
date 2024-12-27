package com.example.basic.dao;

import com.example.basic.entity.ExpediaRoomsAmenities;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaRoomsAmenitiesDao {
    void saveBatch(List<ExpediaRoomsAmenities> insertList);
}
