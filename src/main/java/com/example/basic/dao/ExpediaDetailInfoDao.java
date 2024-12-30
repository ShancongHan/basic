package com.example.basic.dao;

import com.example.basic.entity.ExpediaDetailInfo;

import java.util.List;

/**
 * @author han
 * @date 2024/12/26
 */
public interface ExpediaDetailInfoDao {
    void saveBatch(List<ExpediaDetailInfo> insertList);

    void update(ExpediaDetailInfo expediaDetailInfo);
}
