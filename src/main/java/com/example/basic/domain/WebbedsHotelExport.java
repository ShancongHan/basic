package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/9/4
 */
@Data
public class WebbedsHotelExport {

    @ExcelProperty(value = "酒店ID")
    private String hotelId;

    @ExcelProperty(value = "酒店名字")
    private String hotelName;

    @ExcelProperty(value = "地址")
    private String address;

    @ExcelProperty(value = "映射关系")
    private String sale;

    @ExcelProperty(value = "10/15有价")
    private String hasPrice;
}
