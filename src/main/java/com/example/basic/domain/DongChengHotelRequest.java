package com.example.basic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/22
 */
@Data
public class DongChengHotelRequest {

    @JsonProperty(value = "HotelID")
    private String hotelID ;//string Y 酒店ID

    @JsonProperty(value = "CityId")
    private Integer cityId ;//int Y 城市ID（GetCitiesList接口cid字段）

    @JsonProperty(value = "HotelBrand")
    private String hotelBrand ;//string Y 酒店品牌

    @JsonProperty(value = "PageSize")
    private Integer pageSize ;//int 页面大小（默认30）

    @JsonProperty(value = "PageIndex")
    private Integer pageIndex ;//int 页码
}
