package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/12/26
 */
@Data
public class ExpediaPolicy {
    private String hotelId;	// 酒店id

    private String allDayCheckin;	// 24小时checkIn

    private String checkinStart;	// 开始办理入住

    private String checkinEnd;	// 停止办理入住

    private String checkoutTime;	// 退房

    private Integer minAge;	// 入住最小年龄

    private String specialInstructionsEn;	// 特别入住说明(英文)

    private String specialInstructions;	// 特别入住说明

    private String petsEn;	// 宠物政策(英文)

    private String pets;	// 宠物政策

    private String onsitePayments;	// 该住宿接受的付款方式id(,分割)

    private String onsitePaymentsCurrency;	// 货币

    private Date createTime;	// 系统新增时间

    private Date updateTime;	// 系统更新时间
}
