package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/11/18
 */
@Data
public class WstHotelFacilities {
    private Long id;	// 主键id

    private Long hotelId;	// wst酒店id

    private String category;	// 酒店设施类别：HOTEL_SERVICE：酒店服务;HOTEL_FACILITY:酒店设施

    private Integer facilityId;	// 设施服务编号ID

    private String facilityName;	// 设施服务名称，属性中文名

    private String facilityValue;	// 设施属性值

    private String createName;	// 创建人

    private Date createDate;	// 创建时间

    private String updateName;	// 更新人

    private Date updateDate;	// 更新时间
}
