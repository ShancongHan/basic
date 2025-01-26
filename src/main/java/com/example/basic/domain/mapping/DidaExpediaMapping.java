package com.example.basic.domain.mapping;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2025/1/21
 */
@Data
public class DidaExpediaMapping {

    @ExcelProperty(value = "DidaHotelID")
    private String didaId;

    @ExcelProperty(value = "EPS HotelID")
    private String expediaId;
}
