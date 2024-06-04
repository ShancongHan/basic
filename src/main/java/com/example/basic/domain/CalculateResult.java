package com.example.basic.domain;

import com.example.basic.entity.JdJdbDaolv;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author han
 * @date 2024/5/31
 */
@Data
public class CalculateResult {

    /**
     * 分数-酒店列表
     */
    private Map<Integer, List<JdJdbDaolv>> scoreMap;

    /**
     * 0分
     */
    private Boolean zero;
}
