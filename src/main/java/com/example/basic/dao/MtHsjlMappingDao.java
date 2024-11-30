package com.example.basic.dao;

import com.example.basic.entity.MtHsjlMapping;

import java.util.List;

/**
 * @author han
 * @date 2024/11/27
 */
public interface MtHsjlMappingDao {
    void saveBatch(List<MtHsjlMapping> list);
}
