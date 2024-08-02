package com.example.basic.dao;


import com.example.basic.entity.ExpediaAmenitiesRooms;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaAmenitiesRoomsDao {

    List<ExpediaAmenitiesRooms> selectAll();

    void saveBatch(List<ExpediaAmenitiesRooms> insertList);
}
