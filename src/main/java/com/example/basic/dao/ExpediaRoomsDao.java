package com.example.basic.dao;

import com.example.basic.entity.ExpediaRooms;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaRoomsDao {
    void saveBatch(List<ExpediaRooms> insertList);
}
