package com.example.basic.dao;

import com.example.basic.domain.expedia.CityMatchResult;
import com.example.basic.entity.WstHotelGlobalCity;

import java.util.List;

/**
 * @author han
 * @date 2025/1/10
 */
public interface WstHotelGlobalCityDao {
    List<WstHotelGlobalCity> selectAll();

    void update(CityMatchResult result);
}
