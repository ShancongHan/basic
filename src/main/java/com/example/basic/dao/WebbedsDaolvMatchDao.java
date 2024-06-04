package com.example.basic.dao;

import com.example.basic.entity.WebbedsDaolvMatch;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/5/29
 */
public interface WebbedsDaolvMatchDao {

    void saveBatch(List<WebbedsDaolvMatch> insertList);

    List<WebbedsDaolvMatch> selectDaolv();

    void update(WebbedsDaolvMatch webbedsDaolvMatch);

    Set<Integer> alreadySuccessMatchId();
}
