package com.example.basic.dao;

import com.example.basic.entity.DidaHotelPolicy;

import java.util.List;

/**
 * @author han
 * @date 2025/2/13
 */
public interface DidaHotelPolicyDao {
    void saveBatch(List<DidaHotelPolicy> list);

    void update(DidaHotelPolicy policy);
}
