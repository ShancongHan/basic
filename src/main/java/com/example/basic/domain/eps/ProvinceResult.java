package com.example.basic.domain.eps;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/12
 */
@Data
public class ProvinceResult {

    private Integer provinceId;    // 省份id

    private String provinceName;    // 省份名称

    private String provinceEnName;    // 省份英文名称

    private String epsRegionId;    // EPS区域id
    private String epsProvinceName;    // EPS省份名称

    private String epsProvinceEnName;    // EPS省份英文名称
    private String epsCountryCode;	// EPS国家二字码
}
