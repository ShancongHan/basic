package com.example.basic.dao;

import com.example.basic.entity.WstHotelInfo;

import java.util.List;

/**
 * @author han
 * @date 2024/11/14
 */
public interface WstHotelInfoDao {

    void saveBatch(List<WstHotelInfo> list);

    List<WstHotelInfo> selectProvince();

    List<WstHotelInfo> selectAllIdMatch();

    List<WstHotelInfo> selectCityByProvince(Integer code);

    List<WstHotelInfo> selectAreaByCity(Integer code);

    List<WstHotelInfo> selectBusinessAreaByArea(Integer code);

    List<WstHotelInfo> selectGroupList();
}
