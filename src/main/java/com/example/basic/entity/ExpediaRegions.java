package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/9/23
 */
@Data
public class ExpediaRegions {

    private Long id;	// id

    private String regionId;	// 区域id

    private String type;	// 类型

    private String name;	// 名称

    private String fullName;	// 长名称

    private String nameEn;	// 英文名称

    private String fullNameEn;	// 英文长名称

    private String continent;	// 大洲

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

    private Boolean realExist;	// 真实存在-1，否则0
    private Boolean hasEn;	// 英文可以查询到-1，否则0
    private Boolean hasZh;	// 中文可以查询到-1，否则0
    private Boolean complete;	// 数据完整1-完整;0-不完整
    private Boolean hasProperty;	// 有酒店1-有;0-没有
    private Integer propertyCount;	// 酒店个数
}
