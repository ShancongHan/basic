package com.example.basic.domain.eps;

import lombok.Data;

/**
 * @author han
 * @date 2025/1/2
 */
@Data
public class RegionResult {

    /**
     * 没有字段的列表
     */
    private String notFieldRegions;

    /**
     * 城市超过2个
     */
    private String cityOverTwo;

    /**
     * 无效的城市
     */
    private String invalidCity;

    /**
     * 省份超过5个
     */
    private String provinceOverFive;

    /**
     * 无效的省份
     */
    private String invalidProvince;
}
