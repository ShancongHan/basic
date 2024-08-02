package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class AgeCategories {
    /**
     * 通常是指第一个儿童的年龄要求
     */
    private Age ChildAgeA;

    /**
     * 成人
     */
    private Age Adult;
}
