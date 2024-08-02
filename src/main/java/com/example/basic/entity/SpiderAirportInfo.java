package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/7/1
 */
@Data
public class SpiderAirportInfo {
    private Long id;	// id

    private String countryCode;	// 国家二字码

    private String countryName;	// 国家中文名

    private String countryNameEn;	// 国家英文名

    private String cityName;	// 城市中文名

    private String cityNameEn;	// 城市英文名

    private String airportCode;	// 机场三字码

    private String airportName;	// 机场中文名

    private String airportNameEn;	// 机场英文名
}
