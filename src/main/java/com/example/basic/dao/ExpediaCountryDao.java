package com.example.basic.dao;

import com.example.basic.entity.ExpediaCountry;

import java.util.List;

/**
 * @author han
 * @date 2024/7/30
 */
public interface ExpediaCountryDao {

    void saveBatch(List<ExpediaCountry> insertList);
}
