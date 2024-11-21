package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/11/18
 */
@Data
public class WstHotelStatistics {
    private Long id;	// 主键id

    private Long hotelId;	// wst酒店id

    private String type;	// 酒店统计信息类型:ROOM_COUNT：客房总数;FLOOR_COUNT：酒店楼层高度;MEETING_ROOM_COUNT：会议室数量;RESTAURANT_COUNT：餐厅数量

    private String value;	// 酒店统计信息字段返回的统计值

    private String createName;	// 创建人

    private Date createDate;	// 创建时间

    private String updateName;	// 更新人

    private Date updateDate;	// 更新时间
}
