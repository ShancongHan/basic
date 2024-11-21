package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/11/14
 */
@Data
public class WstHotelCity {

    private Long id;	// 主键id

    private Integer code;	// code

    private String name;	// 名称

    private Integer parent;	// 上级id

    private Integer type;	// 类型：1-省份，2-市，3-区/县/县级市，4-地标

}
