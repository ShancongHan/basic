package com.example.basic.dao;

import com.example.basic.entity.ExpediaAttributesPets;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaAttributesPetsDao {

    List<ExpediaAttributesPets> selectAll();

    void saveBatch(List<ExpediaAttributesPets> insertList);
}
