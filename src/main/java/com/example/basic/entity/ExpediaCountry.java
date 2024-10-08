package com.example.basic.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2024/7/30
 */
@Data
public class ExpediaCountry {
    private Long id;	// id

    @ExcelProperty(value = "ID")
    private String expediaId;	// 国家id

    @ExcelProperty(value = "名称")
    private String name;	// 国家名称

    @ExcelProperty(value = "Name")
    private String nameEn;	// 国家英文名称

    private String code;

    private List<String> propertyIds;
}
