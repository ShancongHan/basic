package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalChain;

import java.util.List;

/**
 * @author han
 * @date 2024/12/25
 */
public interface WstHotelGlobalChainDao {
    void saveBatch(List<WstHotelGlobalChain> list);
}
