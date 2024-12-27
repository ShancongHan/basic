package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/12/26
 */
@Data
public class ExpediaAmenities {
    private Long id;	// 主键id

    private String hotelId;	// 酒店id

    private Integer amenitiesId;	// 设施id

    private String amenitiesNameEn;	// 设施名称(英文)

    private String amenitiesName;	// 设施名称

    private String amenitiesValue;	// 设施值

    private String amenitiesCategories;	// 设施分类

    private Date createTime;	// 系统新增时间

    private Date updateTime;	// 系统更新时间
}
