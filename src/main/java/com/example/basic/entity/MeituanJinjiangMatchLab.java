package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/11/12
 */
@Data
public class MeituanJinjiangMatchLab {

    private Long id;	// id

    private Long mtId;	// 美团酒店id

    private String mtName;	// 美团酒店名称

    private String jinjiangId;	// 锦江酒店Id

    private String jinjiangName;	// 锦江酒店名称

    private BigDecimal mtLatitude;	// 美团纬度

    private BigDecimal mtLongitude;	// 美团经度

    private BigDecimal jinjiangLatitude;	// 锦江纬度

    private BigDecimal jinjiangLongitude;	// 锦江经度

    private String mtAddress;	// 美团地址

    private String jinjiangAddress;	// 锦江地址

    private String mtTel;	// 美团手机号

    private String jinjiangTel;	// 锦江手机号

    private BigDecimal diffMeter;	// 相差距离

    private Integer diffLevel;	// 距离等级

    private Integer score;	// 匹配得分

    private Boolean multiMatch;	// 匹配到多个

    private Integer match;	// 最终匹配:1-true,0-false
}
