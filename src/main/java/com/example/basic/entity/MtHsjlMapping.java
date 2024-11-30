package com.example.basic.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/11/27
 */
@Data
public class MtHsjlMapping {

    @ExcelProperty(value = "sp_hotel_id")
    private String mtIdString;

    @ExcelIgnore
    private Long mtId;

    @ExcelProperty(value = "hotel_id")
    private String hsjlId;
}
