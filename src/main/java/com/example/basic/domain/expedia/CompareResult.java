package com.example.basic.domain.expedia;

import com.example.basic.entity.ExpediaRegions;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author han
 * @date 2025/1/14
 */
@Data
public class CompareResult {

    private Integer cityId;	// 城市id

    private String name;	// 名称

    private String nameEn;	// 英文名称

    /**
     * 唯一得分
     */
    private boolean unique;
    /**
     * 不唯一得分时的列表
     */
    private List<ExpediaRegions> expediaRegions;

    private String epsCityId;	// eps城市id

    private String epsCityType;	// eps城市类别

    private String epsName;	// EPS名称

    private String epsFullName;	// 长名称

    private String epsNameEn;	// EPS英文名称

    private String epsFullNameEn;	// 英文长名称

    private String epsCountryCode;	// EPS国家二字码

    private BigDecimal centerLongitude;	// 中心经度

    private BigDecimal centerLatitude;	// 中心纬度
}
