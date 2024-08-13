package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/13
 */
@Data
public class ExcelFile {
    @ExcelProperty(index = 0)
    private String hotelId;
}
