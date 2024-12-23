package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalCategoryDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/19
 */
public interface WstHotelGlobalCategoryDictionaryDao {

    void saveBatch(List<WstHotelGlobalCategoryDictionary> list);

    void updateByName(WstHotelGlobalCategoryDictionary dictionary);
}
