package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalCountry;

import java.util.List;

/**
 * @author han
 * @date 2024/12/12
 */
public interface WstHotelGlobalCountryDao {
    List<WstHotelGlobalCountry> selectAll();

    void update(WstHotelGlobalCountry country);
}
