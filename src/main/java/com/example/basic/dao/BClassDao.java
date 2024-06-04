package com.example.basic.dao;

import com.example.basic.entity.BClass;

import java.util.List;
import java.util.Map;

/**
 * @author han
 * @date 2024/5/10
 */
public interface BClassDao {
    List<BClass> selectCountryList();

    void saveBatch(List<BClass> insertList);

    void update(BClass bClass);

    Map<String, String> selectIdAndCodeMap();

    List<BClass> selectDuplicateList();
}
