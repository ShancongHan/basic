package com.example.basic.dao;

import com.example.basic.entity.WstHotelFacilities;

import java.util.List;

/**
 * @author han
 * @date 2024/11/18
 */
public interface WstHotelFacilitiesDao {
    void saveBatch(List<WstHotelFacilities> list);
}
