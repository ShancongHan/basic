package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/12
 */
@Data
public class WstHotelGlobalCountry {

    private Long id;	// 主键id

    private String code;	// 国家二字码

    private String name;	// 名称

    private String nameEn;	// 英文名称

    private String epsCode;	// EPS国家二字码

    private String epsName;	// EPS名称

    private String epsNameEn;	// EPS英文名称
}
