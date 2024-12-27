package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/12/26
 */
@Data
public class ExpediaImportantInfo {
    private String hotelId;	// 酒店id

    private String feeMandatory;	// 费用

    private String feeOptional;	// 费用-其他选项

    private String instructionsEn;	// 入住政策原文(英文)

    private String instructions;	// 入住政策原文

    private String knowBeforeYouGoEn;	// 入住须知(英文)

    private String knowBeforeYouGo;	// 入住须知

    private Date createTime;	// 系统新增时间

    private Date updateTime;	// 系统更新时间
}
