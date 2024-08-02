package com.example.basic.dao;

import com.example.basic.entity.ExpediaAmenitiesRates;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaAmenitiesRatesDao {

    List<ExpediaAmenitiesRates> selectAll();

    void saveBatch(List<ExpediaAmenitiesRates> insertList);
}
