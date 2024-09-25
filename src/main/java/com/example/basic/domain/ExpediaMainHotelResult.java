package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/9/25
 */
@Data
public class ExpediaMainHotelResult {

    @ExcelProperty(value = "Property ID")
    private String propertyId;    // 酒店id

    @ExcelProperty(value = "Status")
    private String status;

    @ExcelProperty(value = "Property Name")
    private String name;

    @ExcelProperty(value = "Property Address")
    private String address;

    @ExcelProperty(value = "Property Country")
    private String countryCode;

    @ExcelProperty(value = "Property City")
    private String city;

    @ExcelProperty(value = "longitude")
    private BigDecimal longitude;

    @ExcelProperty(value = "latitude")
    private BigDecimal latitude;

    @ExcelProperty(value = "Property province_code")
    private String state_province_code;

    @ExcelProperty(value = "Property province_name")
    private String state_province_name;

    @ExcelProperty(value = "DIDA Id")
    private Integer hotelId;    // 酒店id

    @ExcelProperty(value = "DIDA Name")
    private String hotelName;

    @ExcelProperty(value = "DIDA Address")
    private String hotelAddress;

    @ExcelProperty(value = "DIDA Country")
    private String hotelCountryCode;

    @ExcelProperty(value = "DIDA City")
    private String hotelCity;

    @ExcelProperty(value = "DIDA longitude")
    private BigDecimal hotelLongitude;

    @ExcelProperty(value = "DIDA latitude")
    private BigDecimal hotelLatitude;
}
