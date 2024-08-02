package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/7/9
 */
@Data
public class SanyiData {

    private Long id;	// id

    private Integer no;	// excel序号

    private String country;	// excel国家名字

    private String city;	// excel城市名字

    private String longitude;	// 经度

    private String latitude;	// 纬度

    private String address;	// 地址

    private String countryCode;	// 国家代码

    private String cityId;	// 城市id

    private String countryMapped;	// 国家是否匹配

    private String cityMapped;	// 国家是否匹配
}
