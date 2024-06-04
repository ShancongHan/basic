package com.example.basic.dao;

import com.example.basic.entity.WebbedsDaolvMatchLabCount;

import java.util.List;

/**
 * @author han
 * @date 2024/5/31
 */
public interface WebbedsDaolvMatchLabCountDao {

    void insert(WebbedsDaolvMatchLabCount matchLabCount);

    List<String> selectDownCountry();
}
