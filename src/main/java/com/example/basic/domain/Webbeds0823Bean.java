package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/23
 */
@Data
public class Webbeds0823Bean {

    @ExcelProperty(value = "DotwHotelcode")
    private String hotelId;

    @ExcelProperty(value = "HotelName")
    private String hotelName;

    @ExcelProperty(value = "DOTWCity")
    private String city;

    @ExcelProperty(value = "CITY CODE")
    private String cityCode;

    @ExcelProperty(value = "Country")
    private String country;

    @ExcelProperty(value = "COUNTRY CODE")
    private String countryCode;

    @ExcelProperty(value = "country short name")
    private String shortCountryName;

    @ExcelProperty(value = "ChainName")
    private String chainName;

    @ExcelProperty(value = "StarRating")
    private String starRating;

    @ExcelProperty(value = "ContactResTel")
    private String tel;

    @ExcelProperty(value = "address")
    private String hotelAddress;

    @ExcelProperty(value = "Latitude")
    private String latitude;

    @ExcelProperty(value = "Longitude")
    private String longitude;

    @ExcelProperty(value = "match condition")
    private String status;
}
