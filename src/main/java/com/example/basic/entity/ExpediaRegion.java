package com.example.basic.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/7/31
 */
@Data
public class ExpediaRegion {

    @ExcelIgnore
    private Long id;	// id

    @ExcelIgnore
    private String regionId;	// 区域id

    @ExcelIgnore
    private String type;	// 类型

    @ExcelIgnore
    private String name;	// 名称

    @ExcelIgnore
    private String nameFull;	// 长名称

    @ExcelIgnore
    private String nameEn;	// 英文名称

    @ExcelIgnore
    private String nameFullEn;	// 英文长名称

    @ExcelIgnore
    private String countryCode;	// 国家二字码

    @ExcelIgnore
    private String countrySubdivisionCode;	// 国家分区代码

    @ExcelIgnore
    private String parentId;	// 父节点

    @ExcelIgnore
    private String parentPath;	// 节点路径

    @ExcelIgnore
    private BigDecimal centerLongitude;	// 中心经度

    @ExcelIgnore
    private BigDecimal centerLatitude;	// 中心纬度

    @ExcelIgnore
    private String categories;	// 分类原文

    @ExcelProperty(value = "标签")
    private String tags;	// 标签原文

    @ExcelIgnore
    private String ancestors;	// 上级节点原文

    @ExcelIgnore
    private String descendants;	// 下级节点原文
}
