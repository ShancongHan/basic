package com.example.basic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/22
 */
@Data
public class DongChengHotel {

    @JsonProperty(value = "HotelId")
    private String hotelId;// string Y 酒店ID

    @JsonProperty(value = "ProvinceName")
    private String provinceName;// int Y 省名称

    @JsonProperty(value = "CityName")
    private String cityName;// string Y 城市名称

    @JsonProperty(value = "BaseInfo")
    private DongChengHotelBaseInfo baseInfo;// uchome_ext_hotel实体 基本信息
}
