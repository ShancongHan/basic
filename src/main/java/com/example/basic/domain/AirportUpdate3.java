package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/5/20
 */
@Data
public class AirportUpdate3 {
    @ExcelProperty(index = 1)
    private String countryCode;

    @ExcelProperty(index = 2)
    private String countryName;

    @ExcelProperty(index = 3)
    private String countryEnName;

    @ExcelProperty(index = 4)
    private String zhou;

    @ExcelProperty(index = 5)
    private String cityName;

    @ExcelProperty(index = 6)
    private String cityEnName;

    @ExcelProperty(index = 7)
    private String cityOrAirportName;

    @ExcelProperty(index = 8)
    private String cityOrAirportSmallEnName;

    @ExcelProperty(index = 9)
    private String cityOrAirportHumpName;

    @ExcelProperty(index = 10)
    private String suoXie;

    @ExcelProperty(index = 11)
    private String threeCode;
}
