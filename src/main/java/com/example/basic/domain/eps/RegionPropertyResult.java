package com.example.basic.domain.eps;

import lombok.Data;

/**
 * @author han
 * @date 2025/1/3
 */
@Data
public class RegionPropertyResult {

    /**
     * id
     */
    private Long id;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 有酒店1-有;0-没有
     */
    private Boolean hasProperty;

    /**
     * 酒店个数
     */
    private Integer propertyCount;

    /**
     * 酒店id字符串(,分割)
     */
    private String propertyIds;

}
