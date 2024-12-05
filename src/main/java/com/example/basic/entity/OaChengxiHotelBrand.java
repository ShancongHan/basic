package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/12/4
 */
@Data
public class OaChengxiHotelBrand {
    private Integer id;	// 主键id

    private String brandId;	// 品牌id

    private String name;	// 品牌名称

    private String sysBrandId;	// 系统品牌id

    private String sysBrandName;	// 系统品牌名称

    private String sysGroupId;	// 系统集团id

    private String sysGroupName;	// 系统集团id

    private Integer belongToId;	// 归属ID 0其他品牌 1快捷连锁 2高端连锁 4中端连锁

    private String belongToName;	// 归属名 0其他品牌 1快捷连锁 2高端连锁 4中端连锁

    private Integer belongToType;	// 主题来源（0-国内海外共用 1-国内专用 2-海外专用）

    private String code;	// Code

    private String isEnable;	// 是否可订

    private String pinyin;	// 品牌拼音

    private Integer groupId;	// 上级集团ID

    private String groupName;	// 上级集团Name

    private String isRecommend;	// 是否推荐，用于高亮显示 T推荐

    private Integer featureType;	// 主题类型（0-主题 1-品类）

    private Integer sortRank;	// 排序等级

    private BigDecimal value;	// 分值
}
