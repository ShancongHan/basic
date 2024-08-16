package com.example.basic.dao;

import com.example.basic.domain.WebbedsMappingExport;
import com.example.basic.entity.ZhJdJdbGjMapping;

import java.util.List;

/**
 * @author han
 * @date 2024/6/20
 */
public interface ZhJdJdbGjMappingDao {

    List<ZhJdJdbGjMapping> selectAll();

    void insertBatch(List<ZhJdJdbGjMapping> insertList);

    int selectMatchCount(List<String> hotelIds);

    List<ZhJdJdbGjMapping> selectWebbedsData();

    List<WebbedsMappingExport> selectWebbeds();

    List<ZhJdJdbGjMapping> selectDaolvOrWebbedsMapping(String webbedsHotelId, String daolvHotelId);
}
