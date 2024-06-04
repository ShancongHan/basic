package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/5/28
 */
@Data
public class JdJdbDaolv {

    private Integer id;	// 酒店id

    private String name;	// 酒店名称

    private String nameCn;	// 酒店中文名称

    private String address;	// 酒店详细地址

    private String cityId;	// 城市id

    private String cityName;	// 城市名称

    private String cityNameCn;	// 城市中文名称

    private String stateCode;	// 省份或州代码

    private String countryCode;	// 国家代码

    private String countryName;	// 国家名称

    private String countryNameCn;	// 国家中文名称

    private String zipCode;	// 邮编

    private BigDecimal longitude;	// 经度

    private BigDecimal latitude;	// 纬度

    private BigDecimal starRating;	// 星级

    private String telephone;	// 电话号码

    private String airportCode;	// 机场代码

    private Integer propertyCategory;	// 酒店类别

    private Long destinationId;	// 目的地id

    private String destinationName;	// 目的地名称

    private String destinationNameCn;	// 目的地中文名称

    private String addressCn;	// 酒店中文详细地址

    private String updateDate;	// 修改日期

    private Integer mappping;	// 映射标识 1 已映射 0未映射

    private String imgUrl;	// 图片地址

    private Date savedate;	// 更新时间
}
