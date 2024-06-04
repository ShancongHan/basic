package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/5/28
 */
@Data
public class WebbedsImportBean {

    @ExcelProperty(value = "Region")
    private String region;

    @ExcelProperty(value = "Country")
    private String country;

    @ExcelProperty(value = "ShortCountryName")
    private String shortCountryName;

    @ExcelProperty(value = "CountryCode")
    private String countryCode;

    @ExcelProperty(value = "City")
    private String city;

    @ExcelProperty(value = "CityCode")
    private String cityCode;

    @ExcelProperty(value = "DOTW_HotelCode")
    private String dotwHotelCode;

    @ExcelProperty(value = "HotelName")
    private String hotelName;

    @ExcelProperty(value = "StarRating")
    private String starRating;

    @ExcelProperty(value = "ReservationTelephone")
    private String reservationTelephone;

    @ExcelProperty(value = "HotelAddress")
    private String hotelAddress;

    @ExcelProperty(value = "Latitude")
    private String latitude;

    @ExcelProperty(value = "Longitude")
    private String longitude;

    @ExcelProperty(value = "ChainName")
    private String chainName;

    @ExcelProperty(value = "BrandName")
    private String brandName;

    @ExcelProperty(value = "New Property")
    private String newProperty;

}
