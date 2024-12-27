package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/12/26
 */
@Data
public class ExpediaRooms {
    private Long id;	// 主键id

    private String hotelId;	// 酒店id

    private String roomId;	// 房间id

    private String nameEn;	// 房间名字(英文)

    private BigDecimal areaSquareMeters;	// 面积(平方米)

    private BigDecimal areaSquareFeet;	// 面积(平方英尺)

    private String roomView;	// 房景id(,分割)

    private Integer maxAllowedTotal;	// 最大容纳人数

    private Integer maxAllowedAdults;	// 最大容纳成人树

    private Integer maxAllowedChildren;	// 最大容纳儿童数

    private String ageCategoriesName;	// 年龄分类名称(,分割)

    private String ageCategoriesMinimumAge;	// 年龄分类最低年龄限制(,分割)

    private String bedGroups;	// 房间床型(JSON串)

    private String descriptionsEn;	// 房间简介(英文)

    private String descriptions;	// 房间简介

    private Date createTime;	// 系统新增时间

    private Date updateTime;	// 系统更新时间
}
