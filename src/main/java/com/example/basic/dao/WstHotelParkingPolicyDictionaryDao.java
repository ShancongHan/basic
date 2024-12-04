package com.example.basic.dao;

import com.example.basic.entity.WstHotelParkingPolicyDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/11/30
 */
public interface WstHotelParkingPolicyDictionaryDao {
    void saveBatch(List<WstHotelParkingPolicyDictionary> list);
}
