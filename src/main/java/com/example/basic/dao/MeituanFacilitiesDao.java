package com.example.basic.dao;

import com.example.basic.entity.MeituanFacilities;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/11/15
 */
public interface MeituanFacilitiesDao {

    List<MeituanFacilities> selectIdMap(List<Long> mtIds);

    List<MeituanFacilities> selectAll();

    List<MeituanFacilities> selectLiatByIds(Set<Long> mtIds);
}
