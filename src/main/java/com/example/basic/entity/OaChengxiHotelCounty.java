package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/5
 */
@Data
public class OaChengxiHotelCounty {
    private Integer id;	// 主键id

    private Integer countyId;	// 区/县级市id

    private Integer countyType;	// 1县级市 2区

    private String countyName;	// 区/县级市名称

    private String countyEnName;	// 区/县级市名称英文名

    private String countyCode;	// 县级市code

    private String countyPinyin;	// 县级市pinyin

    private Integer corpTag;	// 0：标准城市信息，1：非标城市信息（只可预订机票）

    private Integer cityId;	// 城市id

    private String sysCityId;	// 系统城市id

    private Integer countryId;	// 国家id

    private String sysCountryId;	// 系统国家id

    private String sysCountyId;	// 系统行政区id

    private String sysCountyName;	// 系统行政区名称

    private Integer provinceId;	// 省份id
}
