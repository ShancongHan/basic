package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/30
 */
@Data
public class HotelExport {

    @ExcelProperty(value = "酒店ID")
    private String hotelId;

    @ExcelProperty(value = "酒店名字")
    private String hotelName;

    @ExcelProperty(value = "地址")
    private String address;

    @ExcelProperty(value = "电话")
    private String tel;

    @ExcelProperty(value = "latitude")
    private String latitude;

    @ExcelProperty(value = "longitude")
    private String longitude;

    @ExcelProperty(value = "城市")
    private String cityName;

    @ExcelProperty(value = "省份")
    private String provinceName;
}
