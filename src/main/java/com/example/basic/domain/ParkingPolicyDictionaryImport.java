package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/11/30
 */
@Data
public class ParkingPolicyDictionaryImport {

    @ExcelProperty(value = "政策类型")
    private String type;	// 政策类型

    @ExcelProperty(value = "政策类型名称")
    private String name;	// 政策类型名称

    @ExcelProperty(value = "政策值描述")
    private String value;	// 值
}
