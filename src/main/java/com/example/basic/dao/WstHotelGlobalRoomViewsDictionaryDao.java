package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalRoomViewsDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/23
 */
public interface WstHotelGlobalRoomViewsDictionaryDao {
    void saveBatch(List<WstHotelGlobalRoomViewsDictionary> list);
    void updateByName(WstHotelGlobalRoomViewsDictionary dictionary);
}
