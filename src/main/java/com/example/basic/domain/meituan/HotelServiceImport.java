package com.example.basic.domain.meituan;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/11/30
 */
@Data
public class HotelServiceImport {

    @ExcelProperty(value = "属性id")
    private String id;	// 属性名称

    @ExcelProperty(value = "属性中文名")
    private String name;	// 属性名称

    @ExcelProperty(value = "类型信息")
    private String type;	// 属性名称

    @ExcelProperty(value = "值说明")
    private String value;	//

    @ExcelProperty(value = "备注")
    private String remark;	// 备注

    @ExcelIgnore
    private String descriptions;

}
