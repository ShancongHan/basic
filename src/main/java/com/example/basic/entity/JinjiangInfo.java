package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/11/11
 */
@Data
public class JinjiangInfo {

    private String innId;	// 酒店唯一编号

    private String address;	// 酒店地址

    private Integer bookFlag;	// 是否支持预订 (1 是 0 否)

    private String brandCode;	// 品牌编号

    private Integer businessType;	// 经营类别 (0 自营店 1 管理店 2 加盟店 3 标准店 4 合作店)

    private String cityCode;	// 城市编号

    private String cityName;	// 城市名称

    private Long closeDate;	// 停业时间 (时间戳)

    private String description;	// 描述信息 (富文本)

    private String innEmail;	// 酒店邮箱

    private String innName;	// 酒店名称

    private String innNamePinYin;	// 酒店名称全拼

    private String innPhone;	// 酒店电话

    private String innShortName;	// 酒店短名称

    private String innType;	// 酒店类别 (100经济型酒店 101精品商务酒店 102景区度假酒店 103主题特色酒店 104民族风情酒店)

    private Long openDate;	// 开业时间 (时间戳)

    private Integer restaurant;	// 是否有餐厅 (1 是 0 否)

    private String sourceType;	// 酒店来源

    private Integer starType;	// 星级类别 (0 一星 1 二星 2 三星 3 四星 4无星 -1无星)

    private Integer status;	// 酒店状态 (-1 开发 0 筹建中 1开业 2开业后退筹建 3开业后解约 5下线整改 6售出未下 7转品牌 8征用)

    private Integer supportForeignGuest;	// 可接待外宾 (1 可接待 0 不可接待)

    private Integer total;	// 总数

    private Integer valid;	// 是否有效 (1 是 0 否)

    private String picUrl;	// 酒店图片地址

    private BigDecimal minPrice;	// 最低价

    private Integer bookStatus;	// 是否可预订 (1 可预订 2 不可预订)

    private BigDecimal baiduLag;	// 百度纬度

    private BigDecimal baiduLng;	// 百度经度

    private BigDecimal googleLag;	// Google纬度

    private BigDecimal googleLng;	// Google经度

    private BigDecimal tencentLag;	// 腾讯纬度

    private BigDecimal tencentLng;	// 腾讯经度

    private BigDecimal gaodeLag;	// 高德纬度

    private BigDecimal gaodeLng;	// 高德经度

}
