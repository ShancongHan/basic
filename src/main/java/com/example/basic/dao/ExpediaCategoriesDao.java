package com.example.basic.dao;

import com.example.basic.entity.ExpediaCategories;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaCategoriesDao {

    public List<ExpediaCategories> selectAll();
}
