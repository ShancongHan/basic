package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2025/2/13
 */
@Data
public class DidaHotelRoomImages {

    private Long id;	// 主键id

    private Long hotelId;	// 酒店id

    private Integer roomId;	// 房间id

    private Boolean heroImage;	// 精选封面:1-true,0-false

    private String url;	// 图片url
}
