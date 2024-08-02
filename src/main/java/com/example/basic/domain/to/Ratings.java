package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Ratings {

    /**
     * 物业
     */
    private Property property;

    /**
     * 客人评分
     */
    private Guest guest;
}
