package com.example.basic.dao;

import com.example.basic.entity.SanyiData;

import java.util.List;

/**
 * @author han
 * @date 2024/7/9
 */
public interface SanyiDataDao {

    void saveBatch(List<SanyiData> insertList);

    List<SanyiData> selectAll();

    void update(SanyiData sanyiData);
}
