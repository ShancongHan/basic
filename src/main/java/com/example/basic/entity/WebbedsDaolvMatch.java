package com.example.basic.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/5/29
 */
@Data
public class WebbedsDaolvMatch {
    private Long id;	// id

    private Integer daolvHotelId;	// 道旅酒店id

    private String daolvHotelName;	// 道旅酒店名称

    private Integer dotwHotelCode;	// webbeds酒店code

    private String dotwHotelName;	// webbeds酒店名称

    private String webbedsLatitude;	// 经度

    private String webbedsLongitude;	// 纬度

    private String daolvLatitude;	// 经度

    private String daolvLongitude;	// 纬度

    private BigDecimal diffMeter;	// 相差距离

    private Integer nameMatch;	// 酒店名称是否匹配
}
