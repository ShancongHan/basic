package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/8/23
 */
@Data
public class Webbeds08232Bean {

    @ExcelProperty(value = "DIDA Hotel ID")
    private String daolvHotelId;

    @ExcelProperty(value = "DOTW HOTEL ID")
    private String webbedsHotelId;

    @ExcelProperty(value = "Hotel Name")
    private String hotelName;

    @ExcelProperty(value = "Address")
    private String address;

    @ExcelProperty(value = "Provider Address")
    private String providerAddress;

    @ExcelProperty(value = "Category")
    private String category;

    @ExcelProperty(value = "Zip Code")
    private String zipCode;

    @ExcelProperty(value = "Phone Number")
    private String tel;

    @ExcelProperty(value = "Country")
    private String country;

    @ExcelProperty(value = "Country Code")
    private String countryCode;

    @ExcelProperty(value = "CITY")
    private String city;

    @ExcelProperty(value = "CITY Code")
    private String cityCode;

    @ExcelProperty(value = "Latitude")
    private String latitude;

    @ExcelProperty(value = "Longitude")
    private String longitude;

    @ExcelProperty(value = "匹配结果")
    private String status;

    @ExcelProperty(value = "DIDA Hotel Name")
    private String daolvHotelName;

    @ExcelProperty(value = "DIDA Address")
    private String daolvAddress;

    @ExcelProperty(value = "DIDA Phone Number")
    private String daolvTel;

    @ExcelProperty(value = "DIDA Country")
    private String daolvCountry;

    @ExcelProperty(value = "DIDA Latitude")
    private BigDecimal daolvLatitude;

    @ExcelProperty(value = "DIDA Longitude")
    private BigDecimal daolvLongitude;

    @ExcelProperty(value = "meter")
    private Double meter;
}
