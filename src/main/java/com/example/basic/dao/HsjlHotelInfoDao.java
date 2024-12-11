package com.example.basic.dao;

import com.example.basic.entity.HsjlHotelInfo;

import java.util.List;

/**
 * @author mic
 * @date 2024/12/09
 */
public interface HsjlHotelInfoDao {

  List<HsjlHotelInfo> selectHotelInfoIds(Long startId);

  void saveBatch(List<HsjlHotelInfo> list);
}
