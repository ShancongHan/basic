package com.example.basic.dao;

import com.example.basic.entity.ExpediaAttributesGeneral;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaAttributesGeneralDao {

    List<ExpediaAttributesGeneral> selectAll();

    void saveBatch(List<ExpediaAttributesGeneral> insertList);
}
