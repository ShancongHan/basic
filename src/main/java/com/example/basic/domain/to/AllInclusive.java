package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class AllInclusive {

    /**
     * 全部价格计划支持
     */
    private String all_rate_plans;

    /**
     * 详细说明
     */
    private String details;

    /**
     * 部分价格计划支持
     */
    private String some_rate_plans;
}
