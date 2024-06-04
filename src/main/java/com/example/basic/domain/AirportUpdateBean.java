package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/5/14
 */
@Data
public class AirportUpdateBean {

    @ExcelProperty(value = "机场三字码")
    private String threeCode;

    @ExcelProperty(value = "映射的城市id")
    private String cityId;

    @ExcelProperty(value = "备注")
    private String remark;
}
