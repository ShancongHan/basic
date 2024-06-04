package com.example.basic.dao;

import com.example.basic.entity.JdGlobalGeo;

import java.util.List;

/**
 * @author han
 * @date 2024/5/9
 */
public interface JdGlobalGeoDao {

    void saveBatch(List<JdGlobalGeo> dataList);

    List<JdGlobalGeo> selectCountryList();

    List<JdGlobalGeo> selectProvinceList();

    List<JdGlobalGeo> selectCityList();

    List<JdGlobalGeo> selectProvinceListByCountryCode(String countryCode);

    List<JdGlobalGeo> selectProvinceListByCountryCodes(List<String> geoCodeList);

    List<JdGlobalGeo> selectCountryListByLevelTree(String levelTree);

}
