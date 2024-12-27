package com.example.basic.dao;

import com.example.basic.entity.ExpediaRoomsImages;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaRoomsImagesDao {
    void saveBatch(List<ExpediaRoomsImages> insertList);
}
