package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalImagesDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/23
 */
public interface WstHotelGlobalImagesDictionaryDao {
    void saveBatch(List<WstHotelGlobalImagesDictionary> list);
    void updateByName(WstHotelGlobalImagesDictionary dictionary);

}
