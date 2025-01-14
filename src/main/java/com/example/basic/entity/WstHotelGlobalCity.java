package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2025/1/10
 */
@Data
public class WstHotelGlobalCity {
    private Integer cityId;	// 城市id

    private String name;	// 名称

    private String nameEn;	// 英文名称

    private String cityCode;	// 城市code

    private String pinYin;	// 城市pinyin

    private String districtCode;	// 行政区划代码

    private String countryCode;	// 国家二字码

    private String countryName;	// 国家名称

    private String countryNameEn;	// 国家英文名称

    private Integer provinceId;	// 省份id

    private String provinceName;	// 省份名称

    private String provinceNameEn;	// 省份英文名称

    private Integer parentCityId;	// 上级城市id

    private String parentCityName;	// 上级城市名称

    private Integer cityType;	// 城市类型 3地级市 5县级市

    private String epsCityId;	// eps城市id

    private String epsCityType;	// eps城市类别

    private String epsName;	// EPS名称

    private String epsFullName;	// 长名称

    private String epsNameEn;	// EPS英文名称

    private String epsFullNameEn;	// 英文长名称

    private String epsCountryCode;	// EPS国家二字码

    private BigDecimal centerLongitude;	// 中心经度

    private BigDecimal centerLatitude;	// 中心纬度

    private Integer audit;	// 人工复核:0-未复核;1-复核
}
