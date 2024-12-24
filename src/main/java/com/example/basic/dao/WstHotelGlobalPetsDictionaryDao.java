package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalPetsDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/24
 */
public interface WstHotelGlobalPetsDictionaryDao {
    void saveBatch(List<WstHotelGlobalPetsDictionary> list);
    void updateByName(WstHotelGlobalPetsDictionary dictionary);
}
