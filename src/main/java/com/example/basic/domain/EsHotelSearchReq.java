package com.example.basic.domain;

import lombok.Data;

/**
 * @author han
 * @date 2024/11/27
 */
@Data
public class EsHotelSearchReq {

    /** 酒店名称 */
    private String hotelName;

    /** 酒店电话 */
    private String hotelPhone;

    /** 酒店地址 */
    private String hotelAddress;

    /** 省份名称 */
    private String provinceName;

    /** 城市名称 */
    private String cityName;

    /** 区域名称 */
    private String areaName;

    /** 经纬度-lon经度 */
    private Double lon;

    /** 经纬度-lat纬度 */
    private Double lat;
}
