package com.example.basic.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/7/30
 */
@Data
public class ExpediaContinent {

    private Long id;	// id

    @ExcelProperty(value = "ID")
    private Integer expediaId;	// 大洲id

    @ExcelProperty(value = "chineseName")
    private String name;	// 大洲名称

    @ExcelProperty(value = "Name")
    private String nameEn;	// 大洲英文名称
}
