package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/30
 */
@Data
public class HotelExport {

    @ExcelProperty(value = "酒店ID")
    private String hotelId;

    @ExcelProperty(value = "酒店名字")
    private String hotelName;

    @ExcelProperty(value = "城市")
    private String cityName;
}
