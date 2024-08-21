package com.example.basic.dao;

import java.util.List;

/**
 * @author han
 * @date 2024/8/21
 */
public interface JdJdbMeituanDao {

    List<String> selectIds();

    void updateBatch(List<String> list);
}
