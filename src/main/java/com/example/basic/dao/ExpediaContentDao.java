package com.example.basic.dao;

import com.example.basic.entity.ExpediaContent;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaContentDao {

    void saveBatch(List<ExpediaContent> insertList);

    List<ExpediaContent> selectNeedUpdate(int start, int end);
}
