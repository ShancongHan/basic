package com.example.basic.dao;

import com.example.basic.entity.DidaHotelInfo;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2025/2/13
 */
public interface DidaHotelInfoDao {
    void saveBatch(List<DidaHotelInfo> list);

    Set<Long> selectHotelIds();

    void update(DidaHotelInfo info);
}
