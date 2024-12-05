package com.example.basic.dao;

import com.example.basic.entity.OaChengxiHotelZone;

import java.util.List;

/**
 * @author han
 * @date 2024/12/4
 */
public interface OaChengxiHotelZoneDao {
    List<OaChengxiHotelZone> selectAll();

    void update(OaChengxiHotelZone oaChengxiHotelZone);
}
