package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2025/2/13
 */
@Data
public class DidaHotelInfo {

    private Long hotelId;	// wst酒店id

    private String nameEn;	// 英文名称

    private String name;	// 酒店名称

    private String address;	// 酒店详细地址

    private String countryCode;	// 国家

    private String countryNameEn;	// 英文国家名称

    private String countryName;	// 国家名称

    private Long destinationCode;	// 目的地id

    private String destinationNameEn;	// 目的地英文名称

    private String destinationName;	// 目的地名称

    private BigDecimal longitude;	// 经度

    private BigDecimal latitude;	// 纬度

    private String stateCode;	// 省份或州代码

    private String telephone;	// 电话号码

    private BigDecimal starRating;	// 星级

    private String zipCode;	// 邮编

    private String airportCode;	// 机场代码

    private Integer categoryCode;	// 分类id

    private String categoryEn;	// 英文分类

    private String category;	// 分类

    private String giataCodes;	// GIATA property IDs唯一标识列表

    private String descriptionEn;	// 详细描述

    private String description;	// 详细描述
}
