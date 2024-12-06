package com.example.basic.dao;

import com.example.basic.entity.OaChengxiHotelCounty;

import java.util.List;

/**
 * @author han
 * @date 2024/12/5
 */
public interface OaChengxiHotelCountyDao {
    List<OaChengxiHotelCounty> selectChina();

    void update(OaChengxiHotelCounty oaChengxiHotelCounty);
}
