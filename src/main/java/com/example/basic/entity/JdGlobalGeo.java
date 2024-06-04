package com.example.basic.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/5/9
 */
@Data
public class JdGlobalGeo {

    @ExcelProperty(index = 0)
    private Long id;	// id

    @ExcelProperty(index = 1)
    private String geoCode;	// 编码code

    @ExcelProperty(index = 2)
    private String name;	// 中文名

    @ExcelProperty(index = 3)
    private String nameEn;	// 英文名

    @ExcelProperty(index = 4)
    private String desc;	// 简介

    @ExcelProperty(index = 5)
    private String fullName;	// 全称

    @ExcelProperty(index = 6)
    private String pinyin;	// 拼音

    @ExcelProperty(index = 7)
    private String firstPinyin;	// 拼音首字母

    @ExcelProperty(index = 8)
    private String parentGeoCode;	// 上级编码code

    @ExcelProperty(index = 9)
    private Long parentId;	// 上级编码id

    @ExcelProperty(index = 10)
    private String levelTree;	// 树结构

    @ExcelProperty(index = 11)
    private String region;	// 区划等级

    @ExcelProperty(index = 12)
    private Long level;	// 所属层级

    @ExcelProperty(index = 13)
    private String mark;	// 其他说明
}
