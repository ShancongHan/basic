package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/11/18
 */
@Data
public class MeituanPolicy {
    private Long id;	// 主键id

    private Long mtId;	// 美团酒店id

    private String checkinPolicy;	// 入住政策(JSON串)

    private String checkoutPolicy;	// 离店政策(JSON串)

    private String guestPolicy;	// 住客政策(JSON串)

    private String breakPolicy;	// 早餐政策(JSON串)

    private String petPolicy;	// 宠物政策(JSON串)

    private String parkingPolicies;	// 停车场政策(JSON串)

    private String chargePointPolicies;	// 充电车位政策(JSON串)

    private String childPolicy;	// 儿童及加床政策(JSON串)

    private String createName;	// 创建人

    private Date createDate;	// 创建时间

    private String updateName;	// 更新人

    private Date updateDate;	// 更新时间
}
