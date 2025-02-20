package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/12/25
 */
@Data
public class WstHotelGlobalChain {
    private Integer id;	// id

    private String nameEn;	// 英文名称

    private String name;	// 名称

    private Date updateTime;	// 更新时间
}
