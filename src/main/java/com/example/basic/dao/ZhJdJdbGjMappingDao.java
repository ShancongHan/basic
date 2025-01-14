package com.example.basic.dao;

import com.example.basic.domain.WebbedsMappingExport;
import com.example.basic.entity.ZhJdJdbGjMapping;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    List<String> selectAllDaolvId();

    List<ZhJdJdbGjMapping> selectByDaolvIds(Collection<String> daolvHotelIds);

    List<Long> selectDaolvIds();

    List<Long> selectWebbedsIds();

    List<ZhJdJdbGjMapping> selectDidaList();

    List<ZhJdJdbGjMapping> selectEpsListByLocalIds(List<String> list);

    List<String> selectListByLocalIds(Set<String> list);

    List<ZhJdJdbGjMapping> selectLocalIdByPlatIds(List<String> list);
}
