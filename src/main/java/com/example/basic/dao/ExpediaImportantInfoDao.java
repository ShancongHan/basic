package com.example.basic.dao;

import com.example.basic.entity.ExpediaImportantInfo;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaImportantInfoDao {
    void saveBatch(List<ExpediaImportantInfo> insertList);
}
