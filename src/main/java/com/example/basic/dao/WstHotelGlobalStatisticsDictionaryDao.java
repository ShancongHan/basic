package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalStatisticsDictionary;
import com.example.basic.entity.WstHotelGlobalThemesDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/23
 */
public interface WstHotelGlobalStatisticsDictionaryDao {
    void saveBatch(List<WstHotelGlobalStatisticsDictionary> list);
    void updateByName(WstHotelGlobalStatisticsDictionary dictionary);
}
