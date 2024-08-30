package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/6/20
 */
@Data
public class ZhJdJdbGjMapping {

    private Integer id;	// 主键id 自增

    private Long localId;	// 本地酒店id

    private String platId;	// 平台酒店id

    private Integer plat;	// 所属平台 interfacePlat
}
