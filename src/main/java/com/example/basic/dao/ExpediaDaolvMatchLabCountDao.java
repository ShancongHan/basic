package com.example.basic.dao;

import com.example.basic.entity.ExpediaDaolvMatchLabCount;

import java.util.Set;

/**
 * @author han
 * @date 2024/8/8
 */
public interface ExpediaDaolvMatchLabCountDao {

    Set<String> alreadyMatchCountry();

    void insert(ExpediaDaolvMatchLabCount matchLabCount);
}
