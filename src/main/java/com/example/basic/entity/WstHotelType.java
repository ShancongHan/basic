package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/11/14
 */
@Data
public class WstHotelType {
    private Long id;	// 主键id

    private Integer code;	// 酒店类型id

    private String name;	// 酒店类型名字
}
