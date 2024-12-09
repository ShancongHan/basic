package com.example.basic.dao;

import java.util.List;

import com.example.basic.entity.ElongCity;

/**
 * @author mic
 * @date 2024/12/09
 */
public interface ElongCityDao {

  List<ElongCity> selectCityList();

  void saveBatch(List<ElongCity> list);
}