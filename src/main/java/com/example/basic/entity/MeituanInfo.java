package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/11/11
 */
@Data
public class MeituanInfo {

    private Long id;	// 美团酒店id

    private String name;	// 酒店名称

    private String nameEn;	// 酒店英文名称

    private String phone;	// 酒店联系电话

    private String address;	// 酒店地址

    private String addressEn;	// 酒店英文地址

    private Integer countryCode;	// 国家code

    private String countryName;	// 国家名称

    private Integer provinceCode;	// 省份code

    private String provinceName;	// 省份名称

    private Integer cityCode;	// 城市code

    private String cityName;	// 城市名称

    private Integer areaCode;	// 地区code

    private String areaName;	// 地区名称

    private String businessDistrictsCode;	// 商业圈code

    private String businessDistrictsName;	// 商业圈名称

    private BigDecimal lonGaode;	// 高德经度

    private BigDecimal latGaode;	// 高德维度

    private BigDecimal lonBaidu;	// 百度经度

    private BigDecimal latBaidu;	// 百度纬度

    private BigDecimal latGoogle;	// 谷歌经度

    private BigDecimal lonGoogle;	// 谷歌纬度

    private Integer bookable;	// 酒店是否展示。false:不可展示，true:可展示

    private String openDate;	// 开业时间，格式为yyyy/MM或yyyy/MM/dd

    private String decorationDate;	// 装修时间，格式为yyyy/MM或yyyy/MM/dd

    private String openHours;	// 酒店填写的营业时间，无固定格式，可能是12小时制或24小时制

    private Integer score;	// 酒店评分，目前只有平均评分, 评分值，5分制，取值为实际值*10。如4.2分 = 42

    private Integer starCode;	// 酒店星级code

    private String starName;	// 酒店星级名称

    private Integer brandCode;	// 品牌ID

    private String brandName;	// 品牌名称

    private Integer groupCode;	// 集团ID

    private String groupName;	// 集团名称

    private String themes;	// 酒店主题 多个以,隔开(JSON串)

    private String types;	// 酒店类型 多个以,隔开(JSON串)

    private String paymentMethod;	// 前台可用的支付方式(JSON串)

    private String description;	// 描述信息

    private String createName;	// 创建人

    private Date createDate;	// 创建时间

    private String updateName;	// 更新人

    private Date updateDate;	// 更新时间
}
