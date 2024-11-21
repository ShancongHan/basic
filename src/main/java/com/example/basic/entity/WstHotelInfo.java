package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/11/14
 */
@Data
public class WstHotelInfo {
    private Long hotelId;	// wst酒店id

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

    private Integer businessDistrictsCode;	// 商业圈code

    private String businessDistrictsName;	// 商业圈名称

    private BigDecimal gaodeLongitude;	// 高德经度

    private BigDecimal gaodeLatitude;	// 高德维度

    private BigDecimal baiduLongitude;	// 百度经度

    private BigDecimal baiduLatitude;	// 百度纬度

    private BigDecimal googleLongitude;	// 谷歌经度

    private BigDecimal googleLatitude;	// 谷歌纬度

    private String openDate;	// 开业时间，格式为yyyy/MM或yyyy/MM/dd

    private String decorationDate;	// 装修时间，格式为yyyy/MM或yyyy/MM/dd

    private String openHours;	// 酒店填写的营业时间，无固定格式，可能是12小时制或24小时制

    private String score;	// 酒店评分，5分制(平均分)

    private Integer starCode;	// 酒店星级code

    private String starName;	// 酒店星级名称

    private Integer brandCode;	// 品牌ID

    private String brandName;	// 品牌名称

    private Integer groupCode;	// 集团ID

    private String groupName;	// 集团名称

    private String themes;	// 酒店主题ID 多个以,隔开

    private String types;	// 酒店类型ID 多个以,隔开

    private String paymentMethod;	// 前台可用的支付方式

    private String tags;	// 标签

    private String description;	// 描述信息

    private Integer bookable;	// 美团酒店是否展示.false:不可展示，true:可展示

    private Long mtId;	// 美团ID

    private Long hsjlId;	// 红色加力ID

    private String qiantaoId;	// 千淘ID

    private String elongId;	// 艺龙ID

    private Long didaId;	// 道旅ID

    private String huazhuId;	// 华住ID

    private String jinjiangId;	// 锦江ID

    private String dongchengId;	// 东呈ID

    private Integer status;	// 酒店上架状态：0-正常；1-下架

    private String createName;	// 创建人

    private Date createDate;	// 创建时间

    private String updateName;	// 更新人

    private Date updateDate;	// 更新时间

}
