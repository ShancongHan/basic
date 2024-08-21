package com.example.basic.dao;

import com.example.basic.entity.ZhJdJdbMapping;

import java.util.List;

/**
 * @author han
 * @date 2024/8/16
 */
public interface ZhJdJdbMappingDao {

    List<ZhJdJdbMapping> selectLocalIds();

    void deleteByLocalId(String localId);

    List<String> selectElongIds();

    void deleteElongBatch(List<String> deleteList);

    List<String> selectMeituanIds();

    List<String> selectQiantaoIds();

    List<String> selectHsjlIds();

    void deleteHsjlBatch(List<String> list);

    List<String> selectDaolvIds();

    List<String> selectHuazhuIds();
    List<String> selectDongchengIds();
    List<String> selectJinjiangIds();
}
