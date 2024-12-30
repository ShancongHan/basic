package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/26
 */
@Data
public class ExpediaDetailInfo {

    private String hotelId;	// 酒店id

    private String registryNumber;	// 等级号码

    private String spokenLanguages;	// 语言(,分割)

    private String taxId;	// tax_id

    private String descriptionsEn;	// 酒店描述(英文)(JSON串)

    private String descriptions;	// 酒店描述(JSON串)

    private String rates;	// rates(JSON串)

    private String allInclusiveEn;	// 全包(JSON串)(英文)
    private String allInclusive;	// 全包(JSON串)
}
