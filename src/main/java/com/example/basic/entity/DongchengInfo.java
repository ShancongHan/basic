package com.example.basic.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/11/11
 */
@Data
public class DongchengInfo {


    private Long id;	// id主键

    @ExcelProperty(value = "东呈酒店ID")
    private String hid;	// 酒店id

    private String cityId;	// 城市id

    @ExcelProperty(value = "东呈酒店城市")
    private String cityName;	// 城市名字

    private String provinceName;	// 省份名字

    @ExcelProperty(value = "东呈酒店名字")
    private String name;	// 酒店名称

    private String pinyin;	// 酒店英文名称

    @ExcelProperty(value = "东呈酒店地址")
    private String addr;	// 酒店地址

    @ExcelProperty(value = "东呈酒店电话")
    private String tel;	// 酒店电话

    private String pic;	// 酒店主图地址

    private String pic1;	// 酒店图1地址

    private String pic2;	// 酒店图2地址

    private String pic3;	// 酒店图3地址

    private String pic4;	// 酒店图4地址

    private String pic5;	// 酒店图5地址

    private String diannei;	// 店内设施

    private String kefang;	// 客房设施

    private BigDecimal wxLatitude;	// SOSO、Google地图的纬度坐标（GCJ02地理坐标系）

    private BigDecimal wxLongitude;	// SOSO、Google地图的经度坐标（GCJ02地理坐标系）

    private BigDecimal latitude;	// 百度地图的纬度坐标（DJ地理坐标系）

    private BigDecimal longitude;	// 百度地图的经度坐标（DJ地理坐标系）

    private String wifiPwd;	// WIFI密码

    private String brandName;	// 品牌名称

    private String description;	// 简介

    private Date createTime;	// 新增时间
}
