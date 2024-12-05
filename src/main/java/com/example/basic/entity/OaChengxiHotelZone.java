package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/4
 */
@Data
public class OaChengxiHotelZone {

    private Integer id;	// 主键id

    private String zoneId;	// 商业区ID

    private String zoneCentralName;	// 商业中心名称

    private String zoneName;	// 商业区名称

    private String cityId;	// 城市ID

    private String cityName;	// 城市名称

    private String sysCityId;	// 系统城市id

    private String sysCityName;	// 系统城市名称

    private String sysZoneId;	// 系统商圈编码

    private String sysZoneName;	// 系统商圈名称

}
