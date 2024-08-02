package com.example.basic.dao;

import com.example.basic.entity.ExpediaSpokenLanguages;

import java.util.List;

/**
 * @author han
 * @date 2024/8/2
 */
public interface ExpediaSpokenLanguagesDao {

    List<ExpediaSpokenLanguages> selectAll();

    void saveBatch(List<ExpediaSpokenLanguages> insertList);
}
