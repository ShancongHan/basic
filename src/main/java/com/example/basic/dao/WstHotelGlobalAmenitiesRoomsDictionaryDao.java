package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalAmenitiesRoomsDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/24
 */
public interface WstHotelGlobalAmenitiesRoomsDictionaryDao {

    void saveBatch(List<WstHotelGlobalAmenitiesRoomsDictionary> list);
    void updateByName(WstHotelGlobalAmenitiesRoomsDictionary dictionary);

}
