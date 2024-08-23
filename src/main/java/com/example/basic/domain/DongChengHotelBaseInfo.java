package com.example.basic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/22
 */
@Data
public class DongChengHotelBaseInfo {
    private String name;// string N 分店名称

    private String pinyin;// string Y 拼音

    private String description;// string Y 简介

    private String cityid;// int N 城市ID（GetCitiesList接口cid字段）

    private String addr;// string N 地址

    private String pic;// string N 主图(酒店外观)

    private String pic1;// string Y 图1绝对路径

    private String pic2;// string Y 图2绝对路径

    private String pic3;// string Y 图3绝对路径

    private String pic4;// string Y 图4绝对路径

    private String pic5;// string Y 图5绝对路径

    private String tel;// string Y 电话

    private String diannei;// string Y 店内设施（数据格式：大堂电脑|复印传真|休闲书吧|自助咖啡机|停车场|自助洗衣房|自助营养早餐厅|内有电梯|外宾接待|有早餐）注意：如果渠道与集团协议含早，需要判断此字段是否含“有早餐”信息，如无代表酒店不提供早餐

    private String kefang;// string Y客房设施（数据格式：免费宽带|吹风筒|电话|电热水壶|有线电视|冷暖空调|24小时新风循环|24小时热水供应|电子门锁|叫醒服 务）

    @JsonProperty(value = "wx_Latitude")
    private String wxLatitude;// string Y SOSO、Google地图的纬度坐标（GCJ02地理坐标系）

    @JsonProperty(value = "wx_Longitude")
    private String wxLongitude;// string Y SOSO、Google地图的经度坐标（GCJ02地理坐标系）

    @JsonProperty(value = "Latitude")
    private String latitude;// string Y 百度地图的纬度坐标（DJ地理坐标系）

    @JsonProperty(value = "Longitude")
    private String longitude;// string Y 百度地图的经度坐标（DJ地理坐标系）

    @JsonProperty(value = "WifiPwd")
    private String wifiPwd;// string Y WIFI密码

    @JsonProperty(value = "BrandName")
    private String brandName;// string N品牌（请转换大写匹配）BD：铂顿 BM：柏曼 CSBJ：城市便捷 CSJX：城市精选DY：殿影 FTD：锋态度 JC：瑾程 JT：精途 YC：怡程YM：隐沫 YS：宜尚 YSP：宜尚PLUS

    private String zhoubian;// string Y 周边介绍
}
