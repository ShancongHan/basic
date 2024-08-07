package com.example.basic.dao;

import com.example.basic.entity.ExpediaChainBrands;

import java.util.List;

/**
 * @author han
 * @date 2024/8/7
 */
public interface ExpediaChainBrandsDao {

    void saveBatch(List<ExpediaChainBrands> insertList);
}
