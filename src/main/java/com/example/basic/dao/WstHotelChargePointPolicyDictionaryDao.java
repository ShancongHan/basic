package com.example.basic.dao;

import com.example.basic.entity.WstHotelChargePointPolicyDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/11/30
 */
public interface WstHotelChargePointPolicyDictionaryDao {
    void saveBatch(List<WstHotelChargePointPolicyDictionary> list);
}
