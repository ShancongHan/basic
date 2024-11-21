package com.example.basic.dao;

import com.example.basic.entity.DongchengInfo;

import java.util.List;

/**
 * @author han
 * @date 2024/11/11
 */
public interface DongchengInfoDao {

    List<DongchengInfo> selectAll();

    List<DongchengInfo> selectListByIds(List<String> notMatch);
}
