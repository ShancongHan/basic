package com.example.basic.dao;

import com.example.basic.entity.WstHotelCity;

import java.util.List;

/**
 * @author han
 * @date 2024/11/14
 */
public interface WstHotelCityDao {
    void saveBatch(List<WstHotelCity> list);

    List<WstHotelCity> selectProvinceList();

    List<WstHotelCity> selectCityList();

    List<WstHotelCity> selectAreaList();
}
