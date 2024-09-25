package com.example.basic.dao;

import com.example.basic.entity.ExpediaCountry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author han
 * @date 2024/7/30
 */
public interface ExpediaCountryDao {

    void saveBatch(List<ExpediaCountry> insertList);

    List<ExpediaCountry> selectAll();

    void update(ExpediaCountry expediaCountry);


    void updateCountryCode(@Param(value = "expediaId") String expediaId, @Param(value = "countryCode") String countryCode);

    List<ExpediaCountry> selectAllCode();
}
