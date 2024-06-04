package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/5/31
 */
@Data
public class WebbedsDaolvMatchLabCount {

    private Long id;	// id

    private String countryCode;	// 国家

    private Integer webbedsTotal;	// webbeds酒店总数

    private Integer webbedsNeedMatchCount;	// webbeds酒店参与匹配的数量

    private Integer webbedsNotScoreCount;	// webbeds酒店无得分数量

    private Integer webbedsScoreCount;	// webbeds酒店匹配后得分的数量

    private Integer webbedsUniqueScoreCount;	// webbeds酒店有唯一得分

    private Integer webbedsMultiScoreCount;	// webbeds酒店多个同样得分

    private Integer daolvTotal;	// daolv酒店总数

    private Integer daolvMatchedCount;	// daolv被匹配得分的酒店数量

    private Integer time;	// 比对耗时
}
