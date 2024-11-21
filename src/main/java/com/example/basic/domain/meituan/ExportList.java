package com.example.basic.domain.meituan;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/11/21
 */
@Data
public class ExportList {

    @ExcelProperty(value = "美团POI_ID")
    private Long mtId;	//

    @ExcelProperty(value = "美团POI名称")
    private String name;	// 酒店名称

    @ExcelProperty(value = "城市")
    private String cityName;	// 城市名称
}
