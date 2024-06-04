package com.example.basic.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/5/28
 */
@Data
public class WebbedsDaolvMappingBean {

    //@ExcelProperty(value = "DIDA Hotel ID")
    @ExcelProperty(index = 0)
    private String daolvHotelId;

    //@ExcelProperty(value = "Hotel Name")
    @ExcelProperty(index = 1)
    private String daolvHotelName;

    //@ExcelProperty(value = "DOTW Hotel ID")
    @ExcelProperty(index = 2)
    private String dotwHotelId;

    //@ExcelProperty(value = "Hotel Name")
    @ExcelProperty(index = 3)
    private String dotwHotelName;

    //@ExcelProperty(value = "Latitude")
    @ExcelProperty(index = 17)
    private String Latitude;

    //@ExcelProperty(value = "Longitude")
    @ExcelProperty(index = 18)
    private String Longitude;

    //@ExcelProperty(value = "Giata ID")
    @ExcelProperty(index = 19)
    private String giataId;

    //@ExcelProperty(value = "Mapped")
    @ExcelProperty(index = 20)
    private String mapped;

}
