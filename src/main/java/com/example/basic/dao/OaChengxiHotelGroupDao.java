package com.example.basic.dao;

import com.example.basic.entity.OaChengxiHotelGroup;

import java.util.List;

/**
 * @author han
 * @date 2024/12/4
 */
public interface OaChengxiHotelGroupDao {
    List<OaChengxiHotelGroup> selectAll();

    void update(OaChengxiHotelGroup one);
}
