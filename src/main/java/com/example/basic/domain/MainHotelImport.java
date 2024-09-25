package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/9/24
 */
@Data
public class MainHotelImport {

    @ExcelProperty(value = "Property ID")
    private String propertyId;	// 酒店id
}
