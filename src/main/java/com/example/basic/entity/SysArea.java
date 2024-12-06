package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/5
 */
@Data
public class SysArea {
    private Integer id;	// 主键id

    private String areaCode;	// 行政区id

    private String areaName;	// 行政区名称

    private String cityId;	// 城市id

    private String cityName;	// 城市名字

}
