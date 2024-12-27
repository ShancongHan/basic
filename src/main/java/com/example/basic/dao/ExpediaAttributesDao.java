package com.example.basic.dao;

import com.example.basic.entity.ExpediaAttributes;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaAttributesDao {
    void saveBatch(List<ExpediaAttributes> insertList);
}
