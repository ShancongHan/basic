package com.example.basic.dao;

import com.example.basic.entity.SysGroup;

import java.util.List;

/**
 * @author han
 * @date 2024/12/4
 */
public interface SysGroupDao {
    List<SysGroup> selectAll();
}
