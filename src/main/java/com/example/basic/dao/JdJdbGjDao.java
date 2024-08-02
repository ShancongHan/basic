package com.example.basic.dao;

import com.example.basic.entity.JdJdbGj;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author han
 * @date 2024/5/29
 */
public interface JdJdbGjDao {

    JdJdbGj selectInfo(Integer id);

    List<JdJdbGj> selectInfoByIds(List<Integer> daolvHotelIds);

    List<JdJdbGj> selectPartByCountryAndCityId(@Param(value = "countryCode") String countryCode, @Param(value = "cityId") String cityId);
}
