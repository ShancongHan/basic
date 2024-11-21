package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/11/18
 */
@Data
public class WstHotelImages {

    private Long id;	// 主键id

    private Long hotelId;	// wst酒店id

    private Integer category;	// 图片的类别

    private String title;	// 图片标题。如"客房"、"外观"、"公共设施"、"大厅"、"其他"等

    private String linkSize;	// 图片大小，例如500x700

    private String linkUrl;	// 图片url

    private String linkDescription;	// 图片描述

    private String createName;	// 创建人

    private Date createDate;	// 创建时间

    private String updateName;	// 更新人

    private Date updateDate;	// 更新时间
}
