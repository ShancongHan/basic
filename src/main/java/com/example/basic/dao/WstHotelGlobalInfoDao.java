package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalInfo;

import java.util.List;

/**
 * @author han
 * @date 2025/1/21
 */
public interface WstHotelGlobalInfoDao {

    List<WstHotelGlobalInfo> selectAllId();

    void updateWebbedsId(WstHotelGlobalInfo info);

    void updateDidaId(WstHotelGlobalInfo info);
}
