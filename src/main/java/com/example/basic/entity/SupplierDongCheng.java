package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/8/22
 */
@Data
public class SupplierDongCheng {
    private String id;	// id
    private String hotelId;	// 东呈酒店id

    private String cityId;	// 城市id

    private String cityName;	// 城市名字

    private String provinceName;	// 省份名字

    private String jdmc;	// 酒店名称

    private String jdmcEn;	// 酒店英文名称

    private String jddz;	// 酒店地址

    private String jddh;	// 酒店电话

    private String picUrl;	// 酒店主图地址

    private BigDecimal lonWx;	// SOSO、Google地图的经度坐标（GCJ02地理坐标系）

    private BigDecimal latWx;	// SOSO、Google地图的纬度坐标（GCJ02地理坐标系）

    private BigDecimal lon;	// 百度地图的经度坐标（DJ地理坐标系）

    private BigDecimal lat;	// 百度地图的纬度坐标（DJ地理坐标系）

    private Boolean breakfast;	// 是否含早 1 是 0否

    private String brandName;	// 品牌名称

    private String description;	// 简介

    private Date createTime;	// 新增时间
}
