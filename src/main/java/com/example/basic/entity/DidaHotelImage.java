package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2025/2/13
 */
@Data
public class DidaHotelImage {

    private Long id;	// 主键id

    private Long hotelId;	// 酒店id

    private Boolean heroImage;	// 精选封面:1-true,0-false

    private String captionEn;	// 图片英文标题

    private String caption;	// 图片标题

    private String url;	// 图片url

}
