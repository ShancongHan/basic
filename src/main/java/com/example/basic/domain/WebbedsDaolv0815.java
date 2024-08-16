package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/15
 */
@Data
public class WebbedsDaolv0815 {

    @ExcelProperty(value = "道旅国家代码")
    private String daolvCountry;

    @ExcelProperty(value = "道旅城市代码")
    private String daolvCityCode;

    @ExcelProperty(value = "道旅酒店ID")
    private String daolvHotelId;

    @ExcelProperty(value = "道旅城市")
    private String daolvCountryName;

    @ExcelProperty(value = "DOTWID")
    private String webbedsHotelId;

    @ExcelProperty(value = "DOTW酒店名称")
    private String webbedsHotelName;

    @ExcelProperty(value = "匹配结果")
    private String status;

}
