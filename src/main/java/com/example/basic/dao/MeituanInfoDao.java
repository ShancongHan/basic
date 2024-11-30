package com.example.basic.dao;

import com.example.basic.entity.MeituanInfo;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/11/11
 */
public interface MeituanInfoDao {

    List<MeituanInfo> selectDongchengList();

    List<MeituanInfo> selectHuazhuList();

    List<MeituanInfo> selectDongchengExport();

    List<MeituanInfo> selectJinjiangList();

    Page<MeituanInfo> selectPageList();

    List<MeituanInfo> selectHsjlList();

    List<MeituanInfo> selectListByMtIds(Set<Long> mtIds);
}
