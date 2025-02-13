package com.example.basic.dao;

import com.example.basic.entity.DidaHotelImage;

import java.util.List;

/**
 * @author han
 * @date 2025/2/13
 */
public interface DidaHotelImageDao {
    void saveBatch(List<DidaHotelImage> list);
}
