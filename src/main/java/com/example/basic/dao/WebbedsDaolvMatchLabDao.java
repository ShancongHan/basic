package com.example.basic.dao;

import com.example.basic.entity.WebbedsDaolvMatchLab;

import java.util.List;

/**
 * @author han
 * @date 2024/5/30
 */
public interface WebbedsDaolvMatchLabDao {

    void saveBatch(List<WebbedsDaolvMatchLab> insertList);

    void saveBatch2(List<WebbedsDaolvMatchLab> insertList);

    List<WebbedsDaolvMatchLab> selectHighScoreList();
}
