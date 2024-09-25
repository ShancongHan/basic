package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/9/5
 */
@Data
public class ExpediaContentBasic {

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

    private BigDecimal guest;	// 评分综合分

    private BigDecimal longitude;	// 经度

    private BigDecimal latitude;	// 纬度

    private String telephone;	// 电话号码

    private String fax;	// 传真

    private Long categoryId;	// 分类id

    private String category;	// 分类

    private Long rank;	// 排序

    private Boolean expediaCollect;	// expedia: 1-true,0-false

    private Boolean propertyCollect;	// property: 1-true,0-false

    private String themes;	// 酒店主题

    private String statisticsId;	// 统计信息id

    private String statisticsValues;	// 统计信息值

    private String chainId;	// 连锁id

    private String brandId;	// 品牌id

    private String supplySource;	// 来源

    private String spokenLanguages;	// 支持语言

    private String heroImageMiddle;	// 350px图

    private Date addedTime;	// 加入expedia时间

    private Date updatedTime;	// expedia更新时间

    private Date createTime;	// 新增时间

    /**
     * 真实存在
     */
    private Boolean realExist;
    /**
     * 有中文信息
     */
    private Boolean hasZh;

    /**
     * 有价
     */
    private Boolean hasPrice;

    /**
     * 第一版售卖-1
     */
    private Boolean v1Sale;
    private Boolean match;
}
