package com.example.basic.dao;

import com.example.basic.entity.DidaHotelRoom;

import java.util.List;

/**
 * @author han
 * @date 2025/2/13
 */
public interface DidaHotelRoomDao {
    void saveBatch(List<DidaHotelRoom> list);
}
