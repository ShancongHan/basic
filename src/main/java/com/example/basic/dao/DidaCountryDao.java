package com.example.basic.dao;

import com.example.basic.entity.DidaCountry;

import java.util.List;

/**
 * @author han
 * @date 2025/1/26
 */
public interface DidaCountryDao {

    void saveBatch(List<DidaCountry> list);

    void updateName(DidaCountry country);
}
