package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2025/2/13
 */
@Data
public class DidaHotelPolicy {
    private Long hotelId;	// 酒店id

    private String checkinFrom;	// 入住开始办理时间

    private String checkoutTo;	// 离店办理截至时间

    private String importantNotice;	// 重要通知

    private String description;	// 描述

    private String descriptionEn;	// 英文描述

    private String extraInfoList;	// 额外信息(JSON串)

    private String extraInfoListEn;	// 英文额外信息(JSON串)

}
