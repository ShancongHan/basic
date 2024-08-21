package com.example.basic.dao;

import java.util.List;

/**
 * @author han
 * @date 2024/8/19
 */
public interface JdJdbDao {

    List<String> selectAllIds();

    void deleteBatch(List<String> deleteList);
}
