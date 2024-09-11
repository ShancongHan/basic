package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/9/11
 */
@Data
public class MultiPriceExport {

    @ExcelProperty(value = "国家")
    private String countryCode;

    @ExcelProperty(value = "价格计划1")
    private String rateId1;

    @ExcelProperty(value = "价格计划2")
    private String rateId2;

    @ExcelProperty(value = "价格计划3")
    private String rateId3;

    @ExcelProperty(value = "价格计划4")
    private String rateId4;

    @ExcelProperty(value = "价格计划5")
    private String rateId5;

    @ExcelProperty(value = "价格计划6")
    private String rateId6;

    @ExcelProperty(value = "价格计划7")
    private String rateId7;

    @ExcelProperty(value = "价格计划8")
    private String rateId8;

    @ExcelProperty(value = "价格计划9")
    private String rateId9;

    @ExcelProperty(value = "价格计划10")
    private String rateId10;

    @ExcelProperty(value = "价格计划11")
    private String rateId11;

    @ExcelProperty(value = "价格计划12")
    private String rateId12;

    @ExcelProperty(value = "价格计划13")
    private String rateId13;

    @ExcelProperty(value = "价格计划14")
    private String rateId14;

    @ExcelProperty(value = "价格计划15")
    private String rateId15;

    @ExcelProperty(value = "价格计划16")
    private String rateId16;

    @ExcelProperty(value = "价格计划17")
    private String rateId17;
}
