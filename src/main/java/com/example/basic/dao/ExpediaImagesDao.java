package com.example.basic.dao;

import com.example.basic.entity.ExpediaImages;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaImagesDao {

    List<ExpediaImages> selectAll();

    void saveBatch(List<ExpediaImages> insertList);
}
