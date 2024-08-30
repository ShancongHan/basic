package com.example.basic.dao;

import com.example.basic.entity.JdJdbDongcheng;

import java.util.List;

/**
 * @author han
 * @date 2024/8/21
 */
public interface JdJdbDongchengDao {

    List<String> selectIds();

    void updateBatch(List<String> list);

    List<JdJdbDongcheng> selectMatchList();
}
