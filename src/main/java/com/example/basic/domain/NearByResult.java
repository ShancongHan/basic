package com.example.basic.domain;

import com.example.basic.entity.JdJdbGj;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author han
 * @date 2024/7/10
 */
@Data
public class NearByResult {

    /**
     * 分数-酒店列表
     */
    private Map<Double, List<JdJdbGj>> metersMap;
}
