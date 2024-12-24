package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalAmenitiesRatesDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/24
 */
public interface WstHotelGlobalAmenitiesRatesDictionaryDao {
    void saveBatch(List<WstHotelGlobalAmenitiesRatesDictionary> list);
    void updateByName(WstHotelGlobalAmenitiesRatesDictionary list);
}
