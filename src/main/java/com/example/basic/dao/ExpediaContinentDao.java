package com.example.basic.dao;


import com.example.basic.entity.ExpediaContinent;

import java.util.List;

/**
 * @author han
 * @date 2024/7/30
 */
public interface ExpediaContinentDao {

    void saveBatch(List<ExpediaContinent> insertList);
}
