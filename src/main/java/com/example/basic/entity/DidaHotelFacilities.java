package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2025/2/13
 */
@Data
public class DidaHotelFacilities {
    private Long id;	// 主键id

    private Long hotelId;	// 酒店id

    private String type;	// 类型

    private String description;	// 设施描述

    private String descriptionEn;	// 英文设施描述

    private String value;	// 值

}
