package com.example.basic.dao;

import com.example.basic.entity.MeituanDongchengMatchLab;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/11/11
 */
public interface MeituanDongchengMatchLabDao {

    void saveBatch(List<MeituanDongchengMatchLab> list);

    List<MeituanDongchengMatchLab> selectExport();

    Set<Long> selectNotMatchId();

    List<MeituanDongchengMatchLab> selectMap();
}
