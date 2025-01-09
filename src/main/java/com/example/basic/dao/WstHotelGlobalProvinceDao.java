package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalProvince;

import java.util.List;

/**
 * @author han
 * @date 2024/12/13
 */
public interface WstHotelGlobalProvinceDao {
    List<WstHotelGlobalProvince> selectAll();

    void update(WstHotelGlobalProvince province);

    List<WstHotelGlobalProvince> selectOneByCountryCode(String countryCode);

    List<WstHotelGlobalProvince> selectNeedMatch();
}
