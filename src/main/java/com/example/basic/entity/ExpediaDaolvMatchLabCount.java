package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/8
 */
@Data
public class ExpediaDaolvMatchLabCount {
    private Long id;	// id

    private String countryCode;	// 国家

    private Integer expediaTotal;	// expedia酒店总数

    private Integer expediaScoreCount;	// expedia酒店匹配后得分的数量

    private Integer daolvTotal;	// daolv酒店总数

    private Integer daolvMatchedCount;	// daolv被匹配得分的酒店数量

    private Integer expediaNotScoreCount;	// expedia酒店无得分数量

    private Integer expediaUniqueScoreCount;	// expedia酒店有唯一得分

    private Integer expediaMultiScoreCount;	// expedia酒店多个同样得分

    private Integer time;	// 比对耗时(s)
}
