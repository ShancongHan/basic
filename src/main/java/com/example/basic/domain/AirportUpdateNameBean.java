package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/5/14
 */
@Data
public class AirportUpdateNameBean {

    @ExcelProperty(index = 11)
    private String threeCode;

    @ExcelProperty(index = 9)
    private String airportEnName;
}
