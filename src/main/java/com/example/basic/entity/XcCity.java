package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/12
 */
@Data
public class XcCity {
    private Integer cityId;	// 城市id

    private String cityName;	// 城市名称

    private String cityEnName;	// 城市英文名称

    private String cityCode;	// 城市code

    private String cityPinYin;	// 城市pinyin

    private Integer corpTag;	// 0：标准城市信息，1：非标城市信息（只可预订机票）

    private String districtCode;	// 行政区划代码

    private String countryId;	// 国家id

    private String provinceId;	// 省份id

    private String parcityId;	// 上级城市id

    private String parcityName;	// 上级城市名称

    private Integer cityType;	// 城市类型 3地级市 5县级市
}
