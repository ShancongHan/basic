package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/8/16
 */
@Data
public class ZhJdJdbMapping {
    private Long id;	// 主键

    private Integer interfacePlat;	// 平台标识，对应InterfacePlatEnum

    private String localId;	// 本地酒店ID

    private String platId;	// 平台酒店ID

    private Integer isGnGj;	// 0国际 1国内

    private Integer status;	// 映射状态 0 或空有效 1无效

    private Date savedate;	// 更新时间

    private String cityId;	// 城市ID
}
