package com.example.basic.dao;

import com.example.basic.entity.ExpediaInfo;

import java.util.List;

/**
 * @author han
 * @date 2024/12/25
 */
public interface ExpediaInfoDao {

    void saveBatch(List<ExpediaInfo> insertList);

    List<String> selectNeedUpdate();
}