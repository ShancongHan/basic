package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/5/20
 */
@Data
public class AirportUpdate {

    @ExcelProperty(index = 11)
    private String threeCode;

    @ExcelProperty(index = 1)
    private String countryCode;

    @ExcelProperty(index = 2)
    private String countryName;

    @ExcelProperty(index = 5)
    private String cityName;

    @ExcelProperty(index = 6)
    private String cityEnName;

    @ExcelProperty(index = 9)
    private String airportEnName;
}
