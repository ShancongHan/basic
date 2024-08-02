package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/7/31
 */
@Data
public class ExpediaRegion {

    private Long id;	// id

    private String regionId;	// 区域id

    private String type;	// 类型

    private String name;	// 名称

    private String nameFull;	// 长名称

    private String nameEn;	// 英文名称

    private String nameFullEn;	// 英文长名称

    private String countryCode;	// 国家二字码

    private String countrySubdivisionCode;	// 国家分区代码

    private String parentId;	// 父节点

    private String parentPath;	// 节点路径

    private BigDecimal centerLongitude;	// 中心经度

    private BigDecimal centerLatitude;	// 中心纬度

    private String categories;	// 分类原文

    private String tags;	// 标签原文

    private String ancestors;	// 上级节点原文

    private String descendants;	// 下级节点原文
}
