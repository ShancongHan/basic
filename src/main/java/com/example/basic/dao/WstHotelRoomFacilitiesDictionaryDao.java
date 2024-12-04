package com.example.basic.dao;

import com.example.basic.entity.WstHotelRoomFacilitiesDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/11/30
 */
public interface WstHotelRoomFacilitiesDictionaryDao {
    void saveBatch(List<WstHotelRoomFacilitiesDictionary> list);
}
