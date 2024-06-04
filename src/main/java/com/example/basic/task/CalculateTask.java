package com.example.basic.task;

import com.example.basic.domain.CalculateResult;
import com.example.basic.entity.JdJdbDaolv;
import com.example.basic.entity.WebbedsHotelData;
import com.example.basic.helper.MappingScoreHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author han
 * @date 2024/5/31
 */
public class CalculateTask implements Callable<CalculateResult> {

    private WebbedsHotelData webbedsHotelData;

    private List<JdJdbDaolv> daolvHotels;

    public CalculateTask(WebbedsHotelData webbedsHotelData, List<JdJdbDaolv> daolvHotels) {
        this.daolvHotels = daolvHotels;
        this.webbedsHotelData = webbedsHotelData;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public CalculateResult call() throws Exception {
        Map<Integer, List<JdJdbDaolv>> scoreMap = Maps.newHashMap();
        for (JdJdbDaolv daolvHotel : daolvHotels) {
            Integer score = MappingScoreHelper.calculateScore(webbedsHotelData, daolvHotel);
            if (scoreMap.containsKey(score)) {
                scoreMap.get(score).add(daolvHotel);
            } else {
                scoreMap.put(score, Lists.newArrayList(daolvHotel));
            }
        }
        scoreMap.entrySet().removeIf(k -> k.getKey().equals(0));
        CalculateResult result = new CalculateResult();
        result.setScoreMap(scoreMap);
        return result;
    }
}
