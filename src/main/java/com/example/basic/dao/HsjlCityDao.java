package com.example.basic.dao;

import com.example.basic.entity.HsjlCity;

import java.util.List;

/**
 * @author mic
 * @date 2024/12/09
 */
public interface HsjlCityDao {

  List<HsjlCity> selectCityList();

  void saveBatch(List<HsjlCity> list);
}