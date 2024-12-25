package com.example.basic.dao;

import com.example.basic.entity.WstHotelGlobalBrand;

import java.util.List;

/**
 * @author han
 * @date 2024/12/25
 */
public interface WstHotelGlobalBrandDao {
    void saveBatch(List<WstHotelGlobalBrand> list);
}
