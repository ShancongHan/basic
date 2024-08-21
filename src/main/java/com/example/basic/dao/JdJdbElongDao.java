package com.example.basic.dao;

import java.util.List;

/**
 * @author han
 * @date 2024/8/21
 */
public interface JdJdbElongDao {

    List<String> selectIds();

    void updateBatch(List<String> list);
}
