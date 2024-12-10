package com.example.basic.dao;

import com.example.basic.entity.ElongHotelInfo;

import java.util.List;

/**
 * @author mic
 * @date 2024/12/09
 */
public interface ElongHotelInfoDao {

  List<ElongHotelInfo> selectHotelIds(List<String> hotelIds);

  void saveBatch(List<ElongHotelInfo> list);
}
