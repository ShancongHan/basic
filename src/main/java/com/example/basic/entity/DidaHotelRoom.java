package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2025/2/13
 */
@Data
public class DidaHotelRoom {

    private Long id;	// 主键id

    private Long hotelId;	// 酒店id

    private String roomId;	// 房间id

    private String nameEn;	// 房间名字(英文)

    private String name;	// 房间名字

    private Boolean hasWifi;	// 是否有wifi

    private Boolean hasWindow;	// 是否有窗户

    private Integer maxOccupancy;	// 最大容纳人数

    private String size;	// 面积(平方米)

    private String floor;	// 楼层
}
