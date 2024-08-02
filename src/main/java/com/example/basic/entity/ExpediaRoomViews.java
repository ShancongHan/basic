package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/8/2
 */
@Data
public class ExpediaRoomViews {
    private Long id;	// id

    private String name;	// 名字

    private Date createTime;	// 新增时间
}
