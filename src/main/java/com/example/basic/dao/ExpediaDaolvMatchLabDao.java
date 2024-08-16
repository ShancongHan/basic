package com.example.basic.dao;

import com.example.basic.entity.ExpediaDaolvMatchLab;

import java.util.List;

/**
 * @author han
 * @date 2024/8/8
 */
public interface ExpediaDaolvMatchLabDao {

    void saveBatch(List<ExpediaDaolvMatchLab> insertList);

    void saveBatch2(List<ExpediaDaolvMatchLab> insertList);

    List<ExpediaDaolvMatchLab> select16List();
}
