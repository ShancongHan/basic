package com.example.basic.dao;

import com.example.basic.entity.MeituanHsjlMatchLab;

import java.util.List;

/**
 * @author han
 * @date 2024/11/20
 */
public interface MeituanHsjlMatchLabDao {

    void saveBatch(List<MeituanHsjlMatchLab> list);
}
