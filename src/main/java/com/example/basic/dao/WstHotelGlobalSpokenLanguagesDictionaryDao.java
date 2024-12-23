package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalSpokenLanguagesDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/23
 */
public interface WstHotelGlobalSpokenLanguagesDictionaryDao {
    void saveBatch(List<WstHotelGlobalSpokenLanguagesDictionary> list);
    void updateByName(WstHotelGlobalSpokenLanguagesDictionary dictionary);
}
