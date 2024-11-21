package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/11/20
 */
@Data
public class HotelHsjl {
    private Long hotelId;	// 酒店id

    private String hotelName;	// 酒店名称

    private String hotelEngName;	// 酒店英文名称

    private String address;	// 酒店地址

    private String appearancePicUrl;	// 酒店主图地址

    private String hotelIntroduce;	// 酒店介绍

    private String telephone;	// 酒店电话

    private String fax;	// 酒店传真

    private Integer hotelStar;	// 酒店星级，见字典项【酒店星级】

    private Date praciceDate;	// 开业日期

    private Date fitmentDate;	// 最近装修日期

    private String parentHotelGroup;	// 集团id

    private String parentHotelGroupName;	// 集团名称

    private String plateId;	// 品牌id

    private String plateName;	// 品牌名称

    private String countryCode;	// 国家编码

    private String city;	// 城市编码

    private String cityName;	// 城市名称

    private String distinct;	// 行政区编码

    private String distinctName;	// 行政区名称

    private String business;	// 商业区编码

    private String businessName;	// 商业区名称

    private BigDecimal longitude;	// 百度经度

    private BigDecimal latitude;	// 百度纬度

    private String hotelCategory;	// 酒店一级分类，见字典项【酒店分类】

    private String hotelSubCategory;	// 酒店二级分类，见字典项【酒店分类】

    private String checkInTime;	// 酒店规定入住时间

    private String checkOutTime;	// 酒店规定离店时间

    private Integer roomNum;	// 客房总数

    private Integer applicableGuest;	// 可接待人群，3：仅接待大陆客人；4：仅接待大陆和港澳台客人；5：全球客人；

    private Integer allowsPet;	// 是否允许携带宠物，0-否，1-是

    private String petText;	// 携带宠物描述

    private Integer isPark;	// 是否有停车场，0-否 1-是

    private Integer isChargePark;	// 是否有充电车位，0-否 1-是
}
