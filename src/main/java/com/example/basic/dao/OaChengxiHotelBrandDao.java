package com.example.basic.dao;

import com.example.basic.entity.OaChengxiHotelBrand;

import java.util.List;

/**
 * @author han
 * @date 2024/12/4
 */
public interface OaChengxiHotelBrandDao {
    List<OaChengxiHotelBrand> selectAll();

    void update(OaChengxiHotelBrand oaChengxiHotelBrand);
}
