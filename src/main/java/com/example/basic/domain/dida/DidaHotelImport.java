package com.example.basic.domain.dida;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * {@code @date} 2025/1/27
 */
@Data
public class DidaHotelImport {

    @ExcelProperty(value = "HotelID")
    private String hotelId;

    @ExcelProperty(value = "Name")
    private String name;
    @ExcelProperty(value = "Name_CN")
    private String nameCn;
    @ExcelProperty(value = "CityCode")
    private String cityCode;
    @ExcelProperty(value = "CityName")
    private String cityName;
    @ExcelProperty(value = "CityName_CN")
    private String cityNameCN;
    @ExcelProperty(value = "StateCode")
    private String stateCode;
    @ExcelProperty(value = "CountryCode")
    private String countryCode;
    @ExcelProperty(value = "CountryName")
    private String countryName;
    @ExcelProperty(value = "CountryName_CN")
    private String countryNameCN;
    @ExcelProperty(value = "ZipCode")
    private String zipCode;
    @ExcelProperty(value = "Longitude")
    private String longitude;
    @ExcelProperty(value = "Latitude")
    private String latitude;
    @ExcelProperty(value = "StarRating")
    private String starRating;
    @ExcelProperty(value = "Telephone")
    private String telephone;
    @ExcelProperty(value = "AirportCode")
    private String airportCode;
    @ExcelProperty(value = "PropertyCategory")
    private String propertyCategory;
    @ExcelProperty(value = "DestinationID")
    private String destinationID;
    @ExcelProperty(value = "DestinationName")
    private String destinationName;
    @ExcelProperty(value = "DestinationName_CN")
    private String destinationNameCN;
    @ExcelProperty(value = "Address_CN")
    private String addressCN;
    @ExcelProperty(value = "UpdateDate")
    private String updateDate;
}
