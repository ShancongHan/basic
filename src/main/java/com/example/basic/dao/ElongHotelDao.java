package com.example.basic.dao;

import com.example.basic.entity.ElongHotel;

import java.util.List;

/**
 * @author mic
 * @date 2024/12/09
 */
public interface ElongHotelDao {

  List<ElongHotel> selectHotelList();

  List<ElongHotel> selectHotelIds();

  void saveBatch(List<ElongHotel> list);
}
