package com.example.basic.dao;

import com.example.basic.entity.JdJdbGj;

import java.util.List;

/**
 * @author han
 * @date 2024/5/29
 */
public interface JdJdbGjDao {

    JdJdbGj selectInfo(Integer id);

    List<JdJdbGj> selectInfoByIds(List<Integer> daolvHotelIds);
}
