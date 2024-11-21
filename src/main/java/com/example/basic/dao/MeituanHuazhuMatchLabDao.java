package com.example.basic.dao;

import com.example.basic.entity.MeituanHuazhuMatchLab;

import java.util.List;

/**
 * @author han
 * @date 2024/11/12
 */
public interface MeituanHuazhuMatchLabDao {
    void saveBatch(List<MeituanHuazhuMatchLab> list);

    List<MeituanHuazhuMatchLab> selectMap();
}
