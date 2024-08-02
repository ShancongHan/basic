package com.example.basic.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.NumberFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/7/10
 */
@Data
public class SanyiNearHotel {

    @ExcelIgnore
    private Long id;	// id

    @ExcelProperty("序号")
    private Integer no;	// excel序号

    @ExcelProperty("国家名字")
    private String country;	// excel国家名字

    @ExcelProperty("城市名字")
    private String city;	// excel城市名字
    @ExcelIgnore
    private String longitude;	// 经度
    @ExcelIgnore
    private String latitude;	// 纬度

    @ExcelProperty("地址")
    private String address;	// 地址
    @ExcelIgnore
    private String countryCode;	// 国家代码
    @ExcelIgnore
    private String cityId;	// 城市id

    @ExcelProperty("系统酒店id")
    @NumberFormat(value = "#")
    private Long hotelId;	// 酒店id

    @ExcelProperty("系统酒店名称")
    private String hotelName;	// 酒店名称

    @ExcelProperty("系统酒店英文名称")
    private String hotelNameOriginal;	// 酒店原名称

    @ExcelProperty("系统酒店城市id")
    private String hotelCityId;	// 城市id

    @ExcelProperty("系统酒店城市名称")
    private String hotelCityName;	// 城市名称

    @ExcelProperty("系统酒店国家代码")
    private String hotelCountryCode;	// 国家代码

    @ExcelProperty("系统酒店国家名称")
    private String hotelCountryName;	// 国家名称

    @ExcelProperty("系统酒店经度")
    private BigDecimal hotelLongitude;	// 经度

    @ExcelProperty("系统酒店纬度")
    private BigDecimal hotelLatitude;	// 纬度

    @ExcelProperty("系统酒店星级")
    private BigDecimal starRating;	// 星级

    @ExcelProperty("相差距离")
    private BigDecimal diffMeter;	// 相差距离

    @ExcelProperty("系统酒店酒店详细地址")
    private String hotelAddress;	// 酒店详细地址

    @ExcelProperty("系统酒店电话号码")
    private String telephone;	// 电话号码
}
