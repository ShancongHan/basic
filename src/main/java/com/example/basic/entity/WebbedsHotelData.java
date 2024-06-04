package com.example.basic.entity;

import lombok.Data;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/5/28
 */
@Data
public class WebbedsHotelData {
    private Long id;	// id

    private String region;	// 区域

    private String country;	// 国家名称

    private String shortCountryName;	// 国家二字码

    private Integer countryCode;	// webbeds国家code

    private String city;	// 城市名称

    private Integer cityCode;	// webbeds城市code

    private Integer dotwHotelCode;	// webbeds酒店code

    private String hotelName;	// 酒店名称

    private String starRating;	// 酒店星级

    private String reservationTelephone;	// 预定电话

    private String hotelAddress;	// 酒店地址

    private String latitude;	// 经度

    private String longitude;	// 纬度

    private String chainName;	// 连锁名称

    private String brandName;	// 品牌名称

    private String newProperty;	// 新酒店


}
