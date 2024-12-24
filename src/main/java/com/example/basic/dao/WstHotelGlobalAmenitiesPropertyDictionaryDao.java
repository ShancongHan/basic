package com.example.basic.dao;


import com.example.basic.entity.WstHotelGlobalAmenitiesPropertyDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/24
 */
public interface WstHotelGlobalAmenitiesPropertyDictionaryDao {
    void saveBatch(List<WstHotelGlobalAmenitiesPropertyDictionary> list);
    void updateByName(WstHotelGlobalAmenitiesPropertyDictionary dictionary);
}
