package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalThemesDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/23
 */
public interface WstHotelGlobalThemesDictionaryDao {

    void saveBatch(List<WstHotelGlobalThemesDictionary> list);

    void updateByName(WstHotelGlobalThemesDictionary dictionary);
}
