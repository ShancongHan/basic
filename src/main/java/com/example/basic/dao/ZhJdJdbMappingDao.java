package com.example.basic.dao;

import java.util.List;

/**
 * @author han
 * @date 2024/8/16
 */
public interface ZhJdJdbMappingDao {

    List<String> selectLocalIds();

    void deleteByLocalId(String localId);
}
