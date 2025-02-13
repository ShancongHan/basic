package com.example.basic.dao;

import com.example.basic.entity.DidaHotelFacilities;

import java.util.List;

/**
 * @author han
 * @date 2025/2/13
 */
public interface DidaHotelFacilitiesDao
{
    void saveBatch(List<DidaHotelFacilities> list);
}
