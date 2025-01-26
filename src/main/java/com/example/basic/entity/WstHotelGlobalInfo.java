package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2025/1/21
 */
@Data
public class WstHotelGlobalInfo {

    private Long hotelId;	// wst酒店id

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

    private String zipCode;	// 邮编

    private BigDecimal starRating;	// 星级

    private BigDecimal score;	// 评分(1.0-5.0)

    private BigDecimal longitude;	// Google经度

    private BigDecimal latitude;	// Google纬度

    private String telephone;	// 电话号码

    private String fax;	// 传真号码

    private Integer categoryId;	// 分类id

    private String categoryEn;	// 英文分类

    private String category;	// 分类

    private Integer rank;	// 排序

    private Integer chainId;	// 连锁id

    private String chainNameEn;	// 英文名称

    private Integer brandId;	// 品牌id

    private String brandNameEn;	// 英文名称

    private String themes;	// 主题id(,分割)

    private String heroImage;	// 列表封面

    private String airportNearby;	// 附近机场三字码

    private String expediaId;	// expedia ID

    private Long didaId;	// 道旅ID

    private Long webbedsId;	// webbeds ID

    private Integer status;	// 酒店上架状态：0-正常；1-下架

    private String createName;	// 创建人

    private Date createDate;	// 创建时间

    private String updateName;	// 更新人

    private Date updateDate;	// 更新时间
}
