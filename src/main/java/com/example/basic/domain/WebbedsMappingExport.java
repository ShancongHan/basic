package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/13
 */
@Data
public class WebbedsMappingExport {

    @ExcelProperty(value = "webbeds酒店id")
    private String webbedsHotelId;

    @ExcelProperty(value = "webbeds酒店名称")
    private String hotelName;

    @ExcelProperty(value = "webbeds酒店地址")
    private String hotelAddress;

    @ExcelProperty(value = "wst城市id")
    private String cityId;

    @ExcelProperty(value = "wst城市名称")
    private String cityName;

    @ExcelProperty(value = "wst酒店名字")
    private String name;

    @ExcelProperty(value = "wst酒店地址")
    private String address;
}
