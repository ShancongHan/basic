package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/11/18
 */
@Data
public class WstHotelPolicy {
    private Long id;	// 主键id

    private Long hotelId;	// wst酒店id

    private String checkinStart;	// 入住政策-开始入住时间

    private String checkinEnd;	// 入住政策-截止入住时间

    private String checkoutStart;	// 离店政策-次日离店开始时间00:00~12:30、23:30

    private String checkoutEnd;	// 离店政策-离店时间

    private String checkoutHours;	// 离店政策-固定小时后离店

    private String guestPolicy;	// 住客政策(JSON串)

    private String breakPolicy;	// 早餐政策(JSON串)

    private Integer petPolicy;	// 是否允许带宠物：0-禁止；1-允许，不确认收费情况；2-允许，收费；3-允许，免费；

    private String parkingPolicies;	// 停车场政策(JSON串)

    private String chargePointPolicies;	// 充电车位政策(JSON串)

    private String childPolicy;	// 儿童及加床政策(JSON串)

    private String createName;	// 创建人

    private Date createDate;	// 创建时间

    private String updateName;	// 更新人

    private Date updateDate;	// 更新时间
}
