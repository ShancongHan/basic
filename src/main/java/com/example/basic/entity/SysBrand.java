package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/4
 */
@Data
public class SysBrand {

    private Integer id;	// 主键id

    private String brandId;	// 品牌id

    private String brandName;	// 品牌名称

    private String sysGroupId;	// 系统集团id

    private String sysGroupName;	// 系统集团id
}
