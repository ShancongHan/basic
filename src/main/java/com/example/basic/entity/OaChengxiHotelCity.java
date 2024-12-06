package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/5
 */
@Data
public class OaChengxiHotelCity {
    private Integer id;	// 主键id

    private String cityId;	// 城市id

    private String cityName;	// 城市名称

    private String cityEnName;	// 城市英文名称

    private String cityCode;	// 城市code

    private String cityPinYin;	// 城市pinyin

    private Integer corpTag;	// 0：标准城市信息，1：非标城市信息（只可预订机票）

    private String districtCode;	// 行政区划代码

    private String sysCityId;	// 系统城市id

    private String countryId;	// 国家id

    private String countryName;	// 国家名称

    private String countryEnName;	// 国家英文名称

    private String sysCountryId;	// 系统国家id

    private String sysCountryName;	// 系统国家名称

    private String provinceId;	// 省份id

    private String provinceName;	// 省份名称

    private String sysProvinceId;	// 系统省份id

    private String sysProvinceName;	// 系统省份名称

    private String parcityId;	// 上级城市id

    private String parcityName;	// 上级城市名称

    private Integer cityType;	// 城市类型 3地级市 5县级市
}
