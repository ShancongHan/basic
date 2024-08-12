package com.example.basic.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/8/8
 */
@Data
@Accessors(chain = true)
@Builder
public class ExpediaDaolvMatchLab {

    private Long id;	// id

    private Integer daolvHotelId;	// 道旅酒店id

    private String daolvHotelName;	// 道旅酒店名称

    private String expediaHotelId;	// expedia酒店id

    private String expediaHotelName;	// expedia酒店名称

    private String expediaLatitude;	// expedia纬度

    private String expediaLongitude;	// expedia经度

    private String daolvLatitude;	// 纬度

    private String daolvLongitude;	// 经度

    private String expediaAddress;	// expedia地址

    private String daolvAddress;	// daolv地址

    private String expediaTel;	// expedia手机号

    private String daolvTel;	// daolv手机号

    private String expediaCountry;	// expedia国家

    private String daolvCountry;	// daolv国家

    private String expediaCategory;	// expedia分类

    private BigDecimal diffMeter;	// 相差距离

    private Integer diffLevel;	// 距离等级

    private Integer nameMatch;	// 酒店名称是否匹配

    private Integer addressMatch;	// 酒店地址是否匹配

    private Integer telMatch;	// 酒店电话是否匹配

    private Integer score;	// 匹配得分

    private Integer multiMatch;	// 匹配到多个
}
