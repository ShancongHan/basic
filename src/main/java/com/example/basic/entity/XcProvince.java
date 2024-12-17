package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/12
 */
@Data
public class XcProvince {
    private Integer provinceId;	// 省份id

    private String provinceName;	// 省份名称

    private String provinceEnName;	// 省份英文名称

    private String countryId;	// 国家id
}
