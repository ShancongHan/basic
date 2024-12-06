package com.example.basic.domain.meituan;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/11/13
 */
@Data
public class CityImport {

    @ExcelProperty(value = "province_id")
    private Integer provinceId;

    @ExcelProperty(value = "province_name")
    private String provinceName;

    @ExcelProperty(value = "city_id")
    private Integer cityId;

    @ExcelProperty(value = "city_name")
    private String cityName;

    @ExcelProperty(value = "location_id")
    private Integer locationId;

    @ExcelProperty(value = "location_name")
    private String locationName;
}
