package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalGeneralDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/24
 */
public interface WstHotelGlobalGeneralDictionaryDao {
    void saveBatch(List<WstHotelGlobalGeneralDictionary> list);
    void updateByName(WstHotelGlobalGeneralDictionary dictionary);
}
