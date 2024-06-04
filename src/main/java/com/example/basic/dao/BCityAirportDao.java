package com.example.basic.dao;

import com.example.basic.entity.BCityAirport;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/5/11
 */
public interface BCityAirportDao {
    List<BCityAirport> selectAll();

    void updateCityId(BCityAirport bCityAirport);

    List<BCityAirport> selectListByThreeCodes(Set<String> threeCodes);

    void updateAirportEnName(BCityAirport cityAirport);

    List<BCityAirport> selectList();

    void updateNewCityId(String cityId, String id);

    List<BCityAirport> selectWantedList();

    void saveBatch(List<BCityAirport> insertList);
}
