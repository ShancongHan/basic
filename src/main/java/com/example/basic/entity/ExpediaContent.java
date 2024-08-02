package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/8/2
 */
@Data
public class ExpediaContent {

    private Long id;	// id

    private String hotelId;	// 酒店id

    private String name;	// 名称

    private String nameEn;	// 英文名称

    private String address;	// 地址

    private String addressEn;	// 英文地址

    private String countryCode;	// 国家二字码

    private String stateProvinceCode;	// province_code

    private String stateProvinceName;	// province_name

    private String city;	// 城市名字

    private String zipCode;	// 邮编

    private BigDecimal starRating;	// 星级

    private BigDecimal longitude;	// 经度

    private BigDecimal latitude;	// 纬度

    private String telephone;	// 电话号码

    private String fax;	// 传真

    private String category;	// 分类

    private String categoryEn;	// 英文分类

    private Long rank;	// 排序

    private String checkInAllDay;	// 24hour checkIn

    private String checkInStartTime;	// checkIn开始时间

    private String checkInEndTime;	// checkIn结束时间

    private String checkInInstructions;	// checkIn入住政策

    private String checkInInstructionsEn;	// checkIn入住政策英文

    private String checkInSpecialInstructions;	// checkIn任何特别说明

    private String checkInSpecialInstructionsEn;	// checkIn任何特别说明英文

    private Integer checkInMinAge;	// checkIn最小年龄

    private String checkOutTime;	// checkOut时间

    private String checkOutTimeEn;	// checkOut时间英文

    private String fees;	// Information related to a property fees.

    private String feesEn;	// Information related to a property fees.

    private String knowBeforeYouGo;	// 入住须知

    private String attributes;	// 宠物属性和物业属性

    private String attributesEn;	// 宠物属性和物业属性英文

    private String amenities;	// 设施

    private String amenitiesEn;	// 设施英文

    private String images;	// 图片

    private String rooms;	// 房型

    private String roomsEn;	// 房型英文

    private String rates;	// 价格计划

    private String ratesEn;	// 价格计划英文

    private String addedTime;	// 加入expedia时间

    private String updatedTime;	// expedia更新时间

    private String descriptions;	// 酒店描述

    private String statistics;	// 房产的统计数据，例如楼层数

    private String airports;	// 推荐机场

    private String themes;	// 酒店主题

    private String allInclusive;	// 全包服务说明

    private String taxId;	// Tax ID.

    private String chain;	// 连锁信息

    private String brand;	// 品牌信息

    private String spokenLanguages;	// 支持语言

    private Date createTime;	// 新增时间
}
