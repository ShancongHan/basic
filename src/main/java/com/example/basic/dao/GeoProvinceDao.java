package com.example.basic.dao;

import com.example.basic.entity.GeoProvince;

import java.util.List;

/**
 * @author han
 * @date 2024/5/10
 */
public interface GeoProvinceDao {
    List<GeoProvince> selectProvinceList();

    void saveBatch(List<GeoProvince> insertList);

    List<GeoProvince> selectProvinceListByCountryCode(String countryCode);

    List<GeoProvince> selectDuplicateList(List<String> nationIdList);

    void deleteByIds(List<Integer> ids);

    List<GeoProvince> selectAll();

    List<GeoProvince> selectProvinceListByCountryCodes(List<String> nationIdList);
}
