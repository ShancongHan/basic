package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/11
 */
@Data
public class OaChengxiCountry {
    private Integer id;	// 主键id

    private String countryId;	// 国家id

    private String countryName;	// 国家名称

    private String countryEnName;	// 英文国家名称

    private String countryCode;	// 国家码

    private String continentId;	// 国家所在洲ID

    private String continentName;	// 国家所在洲名称

    private String areaCode;	// 多返回的字段，接口文档未说明

    private String sysContinentId;	// 系统洲id

    private String sysCountryId;	// 系统国家id
}
