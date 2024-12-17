package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/13
 */
@Data
public class WstHotelGlobalProvince {

    private Integer id;	// 省份id

    private String name;	// 名称

    private String nameEn;	// 英文名称

    private String countryCode;	// 国家二字码

    private String epsName;	// EPS名称

    private String epsNameEn;	// EPS英文名称

    private String epsCountryCode;	// EPS国家二字码
}
