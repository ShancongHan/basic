package com.example.basic.dao;

import com.example.basic.entity.WstHotelPolicy;

import java.util.List;

/**
 * @author han
 * @date 2024/11/18
 */
public interface WstHotelPolicyDao {
    void saveBatch(List<WstHotelPolicy> list);
}
