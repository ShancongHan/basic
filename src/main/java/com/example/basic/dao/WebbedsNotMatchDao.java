package com.example.basic.dao;

import java.util.Set;

/**
 * @author han
 * @date 2024/6/3
 */
public interface WebbedsNotMatchDao {

    void saveBatch(Set<Integer> zeroScores);
}
