package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2025/1/9
 */
@Data
public class MatchResult {

    @ExcelProperty(value = "ttttt")
    private String test;
    @ExcelProperty(value = "道旅id")
    private String didaHotelId;
    @ExcelProperty(value = "道旅酒店名字")
    private String didaHotelName;
    @ExcelProperty(value = "道旅酒店英文名字")
    private String didaHotelNameEn;
    @ExcelProperty(value = "eps酒店id")
    private String epsHotelId;
    @ExcelProperty(value = "eps酒店名称")
    private String epsHotelName;
    @ExcelProperty(value = "eps酒店英文")
    private String epsHotelNameEn;
}
