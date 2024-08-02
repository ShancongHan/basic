package com.example.basic.dao;

import com.example.basic.entity.ExpediaRoomViews;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaRoomViewsDao {

    List<ExpediaRoomViews> selectAll();

    void saveBatch(List<ExpediaRoomViews> insertList);
}
