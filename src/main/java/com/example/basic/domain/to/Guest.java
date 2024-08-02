package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Guest {

    /**
     * 评论总数
     */
    private Integer count;

    /**
     * 总评分
     */
    private String overall;

    /**
     * 干净程度
     */
    private String cleanliness;

    /**
     * 服务
     */
    private String service;

    /**
     * 舒适度
     */
    private String comfort;

    /**
     * 酒店状况
     */
    private String condition;

    /**
     * 位置便利程度
     */
    private String location;

    /**
     * 周边满意度
     */
    private String neighborhood;

    /**
     * 客房质量
     */
    private String quality;

    /**
     * 分数
     */
    private String value;

    /**
     * 便利设施
     */
    private String amenities;

    /**
     * 推荐指数
     */
    private String recommendation_percent;
}
