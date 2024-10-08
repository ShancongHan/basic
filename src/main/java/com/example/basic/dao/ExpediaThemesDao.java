package com.example.basic.dao;

import com.example.basic.entity.ExpediaThemes;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaThemesDao {

    List<ExpediaThemes> selectAll();

    void saveBatch(List<ExpediaThemes> insertList);
}
