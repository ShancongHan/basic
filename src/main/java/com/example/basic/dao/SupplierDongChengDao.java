package com.example.basic.dao;

import com.example.basic.entity.SupplierDongCheng;

import java.util.List;

/**
 * @author han
 * @date 2024/8/22
 */
public interface SupplierDongChengDao {

    void saveBatch(List<SupplierDongCheng> insertList);
}
