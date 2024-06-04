package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/5/11
 */
@Data
public class BCityAirport {

    private String id;	// 机场ID

    private String cityId;	// 城市ID

    private String airportName;	// 机场名称

    private String eName;	// 英文名称

    private String shortname;	// 机场简称

    private String threeCode;	// 三字代码

    private Integer international;	// 国内国际0国际1国内

    private Integer ifGjhb;	// 是否有国际航班

    private String sameThreeCode;	// 同义三字码多个用“/”分隔

    private String lon;	// 经度

    private String lat;	// 纬度

    private String addr;	// 机场地址

    private String introduce;	// 机场简介

    private String fac;	// 机场配套设施

    private String arrivalDesc;	// 到达说明

    private String pyjsm;	// 拼音码（简拼）拼音码（简拼）

    private String fullspell;	// 拼音码（全拼）拼音码（全拼）

    private Integer sn;	// 序号

    private Integer isGk;	// 是否港口（0：否；1：是）

    private Integer type;	// 机场-1  城市-2  站点-港口-景点-3

    private String cuId;	// 创建者创建者

    private String creator;	// 创建者名称

    private Date createdate;	//

    private String muId;	// 更新者更新者

    private String mender;	// 更新者名称

    private Date savedate;	// 更新时间更新时间

    private String remark;	// 备注信息

    private Integer delFlag;	// 删除标记删除标记（0：正常；1：删除）

    private Integer version;	// 数据版本号

    private String cityName;	// 城市名称

    private String cityEnName;	// 英文名称

    private String nation;	// 所在国家id

    private String nationName;	// 所在国家名字

    private String nationCode;	// 所在国家名字
}
