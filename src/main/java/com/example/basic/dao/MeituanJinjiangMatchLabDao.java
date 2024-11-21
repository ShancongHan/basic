package com.example.basic.dao;

import com.example.basic.entity.MeituanJinjiangMatchLab;

import java.util.List;

/**
 * @author han
 * @date 2024/11/12
 */
public interface MeituanJinjiangMatchLabDao {
    void saveBatch(List<MeituanJinjiangMatchLab> list);

    List<MeituanJinjiangMatchLab> selectMap();
}
