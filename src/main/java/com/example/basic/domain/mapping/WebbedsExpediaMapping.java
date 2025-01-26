package com.example.basic.domain.mapping;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2025/1/21
 */
@Data
public class WebbedsExpediaMapping {

    @ExcelProperty(value = "DOTW_HotelCode")
    private String webbedsId;

    @ExcelProperty(value = "Expedia_HotelCode")
    private String expediaId;
}
