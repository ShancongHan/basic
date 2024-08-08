package com.example.basic.dao;

import com.example.basic.entity.ExpediaPropertyBasic;

import java.util.List;

/**
 * @author han
 * @date 2024/8/7
 */
public interface ExpediaPropertyBasicDao {

    void saveBatch(List<ExpediaPropertyBasic> insertList);

    int update(ExpediaPropertyBasic expediaPropertyBasic);
}
