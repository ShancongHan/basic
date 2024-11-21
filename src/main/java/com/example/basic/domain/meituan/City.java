package com.example.basic.domain.meituan;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/11/13
 */
@Data
public class City {

    @ExcelProperty(value = "province_id")
    private String provinceId;

    @ExcelProperty(value = "province_name")
    private String provinceName;

    @ExcelProperty(value = "city_id")
    private String cityId;

    @ExcelProperty(value = "city_name")
    private String cityName;

    @ExcelProperty(value = "location_id")
    private String locationId;

    @ExcelProperty(value = "location_name")
    private String locationName;
}
