package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/7/9
 */
@Data
public class SanYiExcelDataBean {

    @ExcelProperty(index = 0)
    private String no;

    @ExcelProperty(index = 1)
    private String region;
    @ExcelProperty(index = 2)
    private String countryRegion;
    @ExcelProperty(index = 3)
    private String country;
    @ExcelProperty(index = 5)
    private String city;
    @ExcelProperty(index = 7)
    private String longitude;
    @ExcelProperty(index = 8)
    private String latitude;
    @ExcelProperty(index = 9)
    private String address;
}
