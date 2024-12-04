package com.example.basic.dao;

import com.example.basic.entity.WstHotelServiceFacilitiesDictionary;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/11/30
 */
public interface WstHotelServiceFacilitiesDictionaryDao {
    void saveBatch(List<WstHotelServiceFacilitiesDictionary> list);

    Set<String> selectAllId();
}
