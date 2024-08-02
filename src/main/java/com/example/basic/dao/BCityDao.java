package com.example.basic.dao;

import com.example.basic.domain.CityData;
import com.example.basic.entity.BCity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author han
 * @date 2024/5/10
 */
public interface BCityDao {
    List<BCity> selectCityList();

    void deleteByIds(List<String> list);

    void saveBatch(List<BCity> list);

    void updateAirport(List<String> list);

    List<BCity> selectOldList(List<String> toList);

    List<BCity> selectCityListByCountries(List<String> countryList);

    List<BCity> selectList();

    List<CityData> selectDataList();

    List<String> selectCityId(@Param(value = "countryCode") String countryCode, @Param(value = "cityName") String cityName);
}
