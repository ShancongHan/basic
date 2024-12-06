package com.example.basic.dao;

import com.example.basic.entity.SysArea;

import java.util.List;

/**
 * @author han
 * @date 2024/12/5
 */
public interface SysAreaDao {
    List<SysArea> selectAll();

    void updateName(SysArea sysArea);
}
