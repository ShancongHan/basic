package com.example.basic.dao;

import com.example.basic.entity.HsjlHotel;

import java.util.List;

/**
 * @author mic
 * @date 2024/12/09
 */
public interface HsjlHotelDao {

  List<HsjlHotel> selectHotelIds(Long startId);

  void saveBatch(List<HsjlHotel> list);
}
