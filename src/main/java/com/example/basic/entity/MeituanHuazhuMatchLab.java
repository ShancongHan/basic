package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/11/12
 */
@Data
public class MeituanHuazhuMatchLab {
    private Long id;	// id

    private Long mtId;	// 道旅酒店id

    private String mtName;	// 道旅酒店名称

    private String huazhuId;	// 华住酒店Id

    private String huazhuName;	// 华住酒店名称

    private BigDecimal mtLatitude;	// 美团纬度

    private BigDecimal mtLongitude;	// 美团经度

    private BigDecimal huazhuLatitude;	// 华住纬度

    private BigDecimal huazhuLongitude;	// 华住经度

    private String mtAddress;	// 美团地址

    private String huazhuAddress;	// 华住地址

    private String mtTel;	// 美团手机号

    private String huazhuTel;	// 华住手机号

    private BigDecimal diffMeter;	// 相差距离

    private Integer diffLevel;	// 距离等级

    private Integer score;	// 匹配得分

    private Boolean multiMatch;	// 匹配到多个

    private Integer match;	// 最终匹配:1-true,0-false
}
