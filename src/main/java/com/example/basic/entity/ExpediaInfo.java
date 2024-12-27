package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/12/25
 */
@Data
public class ExpediaInfo {

    private String hotelId;	// 酒店id

    private String nameEn;	// 英文名称

    private String name;	// 名称

    private String addressEn;	// 英文地址

    private String address;	// 地址

    private String countryCode;	// 国家二字码

    private String provinceCode;	// 省/州/区/县code

    private String provinceNameEn;	// 省/州/区/县英文名字

    private String provinceName;	// 省/州/区/县名字

    private String cityId;	// 城市id

    private String cityEn;	// 城市英文名字

    private String city;	// 城市名字

    private String wstCityId;	// WST城市id

    private String zipCode;	// 邮编

    private BigDecimal starRating;	// 星级

    private String guest;	// 评分详情(JSON)串

    private BigDecimal score;	// 评分(1.0-5.0)

    private BigDecimal longitude;	// Google经度

    private BigDecimal latitude;	// Google纬度

    private String telephone;	// 电话号码

    private String fax;	// 传真号码

    private Integer categoryId;	// 分类id

    private String categoryEn;	// 英文分类

    private String category;	// 分类

    private Boolean expediaCollect;	// expedia代收: 1-true,0-false

    private Boolean propertyCollect;	// 酒店到付: 1-true,0-false

    private Integer rank;	// 排序

    private Integer chainId;	// 连锁id

    private String chainNameEn;	// 英文名称

    private Integer brandId;	// 品牌id

    private String brandNameEn;	// 英文名称

    private String themes;	// 主题id(,分割)

    private String heroImage;	// 350px图

    private Date addedTime;	// 加入expedia时间(UTC时间)

    private Date updatedTime;	// expedia更新时间(UTC时间)

    private String airportNearby;	// 附近机场三字码

    private String supplySource;	// 来源

    private Date createTime;	// 系统新增时间

    private Date updateTime;	// 系统更新时间
}
