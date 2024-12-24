package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/12/24
 */
@Data
public class WstHotelGlobalGeneralDictionary {

    private Integer id;	// id

    private String nameEn;	// 英文名字

    private String name;	// 名字

    private Boolean hasValue;	// 是否有值1-true;0-false

    private Date sysCreateTime;	// 新增时间

    private Date expediaFileTime;	// eps文件时间
}
