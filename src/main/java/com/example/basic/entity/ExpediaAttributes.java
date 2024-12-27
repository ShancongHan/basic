package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/12/26
 */
@Data
public class ExpediaAttributes {
    private Long id;	// 主键id

    private String hotelId;	// 酒店id

    private Integer generalId;	// 特征id

    private String generalNameEn;	// 特征名称(英文)

    private String generalName;	// 特征名称

    private String generalValue;	// 特征值

    private Date createTime;	// 系统新增时间

    private Date updateTime;	// 系统更新时间
}
