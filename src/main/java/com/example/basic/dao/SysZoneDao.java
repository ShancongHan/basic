package com.example.basic.dao;

import com.example.basic.entity.SysZone;

import java.util.List;

/**
 * @author han
 * @date 2024/12/4
 */
public interface SysZoneDao {
    void saveBatch(List<SysZone> insertList);

    List<SysZone> selectAll();
}
