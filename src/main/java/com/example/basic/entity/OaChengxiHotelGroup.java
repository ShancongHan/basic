package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/12/4
 */
@Data
public class OaChengxiHotelGroup {
    private Integer id;	// 主键id

    private String groupId;	// 集团id

    private String name;	// 集团名称

    private String pinyin;	// 集团拼音

    private String sysGroupId;	// 系统集团code

    private String sysGroupName;	// 系统集团名称

    private Integer belongToId;	// 归属ID 0其他品牌 1快捷连锁 2高端连锁 4中端连锁

    private String belongToName;	// 归属名 0其他品牌 1快捷连锁 2高端连锁 4中端连锁

    private String belongToEnum;	// HIGH_END_CHAIN（高端连锁）；MID_RANGE_CHAIN（中端连锁）；QUICK_CHAIN（快捷连锁）

    private Integer belongToType;	// 主题来源（0-国内海外共用 1-国内专用 2-海外专用）

    private String code;	// Code

    private String isEnable;	// 是否可订

    private String isRecommend;	// 是否推荐，用于高亮显示 T推荐

    private Integer featureType;	// 主题类型（0-主题 1-品类）

    private Integer sortRank;	// 排序等级

    private BigDecimal value;	// 分值

}
