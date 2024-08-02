package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Occupancy {

    /**
     * 最大允许入住
     */
    private MaxAllowed max_allowed;

    /**
     * 年龄分类
     */
    private AgeCategories age_categories;
}
