package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2025/1/9
 */
@Data
public class DidaAndEpsMapping {

    @ExcelProperty(value = "ID")
    private String id;

    @ExcelProperty(value = "EPS HotelID")
    private String epsHotelId;

    @ExcelProperty(value = "DidaHotelID")
    private String didaHotelId;
}
