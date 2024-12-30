package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/26
 */
@Data
public class ExpediaRoomsImages {
    private Long id;	// 主键id

    private String hotelId;	// 酒店id

    private String roomId;	// 房间id

    private Boolean heroImage;	// 精选封面:1-true,0-false

    private String groupEn;	// 分组英文名字

    private String group;	// 分组名字

    private Integer categoryId;	// 图片分类id

    private String categoryEn;	// 图片英文标题

    private String caption;	// 图片标题

    private String smallSizeUrl;	// 350px图片url

    private String largeSizeUrl;	// 1000px图片url
}
