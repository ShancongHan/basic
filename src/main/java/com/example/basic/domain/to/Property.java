package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Property {

    /**
     * 星级
     */
    private String rating;

    /**
     * 类型 Alternate(expedia代替评级)、Star(当地酒店评级机构评级)
     */
    private String type;
}
