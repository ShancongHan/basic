package com.example.basic.dao;

import com.example.basic.entity.JdJdbDaolv;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/5/28
 */
public interface JdJdbDaolvDao {

    List<JdJdbDaolv> selectHavePrice();

    Set<Integer> selectHotelIdList();

    List<JdJdbDaolv> selectListByIds(List<Integer> daolvHotelIds);

    List<JdJdbDaolv> selectListByCountry(String countryCode);

    List<String> selectCountryCodes();

    List<String> selectEffectIds(List<String> daolvHotelIds);

    JdJdbDaolv selectById(Integer daolvHotelId);
}
