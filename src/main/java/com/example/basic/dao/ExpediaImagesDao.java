package com.example.basic.dao;

import com.example.basic.entity.ExpediaImages;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaImagesDao {
    void saveBatch(List<ExpediaImages> insertList);
}
