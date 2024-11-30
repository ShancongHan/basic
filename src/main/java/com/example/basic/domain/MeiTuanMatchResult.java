package com.example.basic.domain;

import com.example.basic.entity.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author han
 * @date 2024/11/11
 */
@Data
public class MeiTuanMatchResult {

    /**
     * 分数-酒店列表
     */
    private Map<Integer, List<MeituanInfo>> scoreMap;


    private Map<Integer, List<HuazhuInfo>> huazhuScoreMap;

    private Map<Integer, List<JinjiangInfo>> jinjiangScoreMap;

    private Map<Integer, List<HotelHsjl>> hsjlScoreMap;

    private Map<Long, Long> mtIdAndHsjlIdMap;

    /**
     * 0分
     */
    private Boolean zero;
}
