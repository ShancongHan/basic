package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/8/2
 */
@Data
public class ExpediaAmenitiesProperty {
    private Long id;	// id

    private String name;	// 名字

    private Integer hasValue;	// 是否有值1-true;0-false

    private Date createTime;	// 新增时间
}
