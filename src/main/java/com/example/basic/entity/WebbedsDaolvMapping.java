package com.example.basic.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author han
 * @date 2024/5/29
 */
@Data
@Accessors(chain = true)
@Builder
public class WebbedsDaolvMapping {
    private Long id;	// id

    private Integer daolvHotelId;	// 道旅酒店id

    private String daolvHotelName;	// 道旅酒店名称

    private Integer dotwHotelCode;	// webbeds酒店code

    private String dotwHotelName;	// webbeds酒店名称

    private String latitude;	// 经度

    private String longitude;	// 纬度

    private String giataId;	// Giata ID

    private String mapped;	// 是否匹配
}
