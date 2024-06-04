package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/5/10
 */
@Data
public class BCity {

    private String id;	// 城市ID

    private String cityName;	// 城市名称

    private String eName;	// 英文名称

    private Integer international;	// 国内国际0国际1国内

    private String zfl;	// 州

    private String nation;	// 所在国家

    private String province;	// 所在省份

    private String introduce;	// 城市简介

    private String areaCode;	// 电话区号

    private String postCode;	// 邮政编码

    private Integer cityType;	// 城市分类1省会城市2直辖市0无3地州市4县级市

    private String parcityId;	// 上级城市id

    private String parcityName;	// 上级城市名称

    private String lon;	// 经度

    private String lat;	// 纬度

    private Integer isAirport;	// 是否有机场

    private Integer isTrainStation;	// 是否有火车站

    private String threeCode;	// 城市三字码

    private Integer isMoreAirport;	// 是否有多个机场

    private String otherThreecode;	// 其它三字码多个用“/”分隔

    private String pyjsm;	// 拼音码(简拼)

    private String elongId;	// 艺龙城市id

    private String fullspell;	// 拼音码(全拼)

    private String cuId;	// 创建者创建者

    private String creator;	// 创建者名称

    private Date createdate;	// 创建时间创建时间

    private String muId;	// 更新者更新者

    private String mender;	// 更新者名称

    private Date savedate;	// 更新时间更新时间

    private String remark;	// 备注信息

    private Integer delFlag;	// 删除标记删除标记（0：正常；1：删除）

    private Integer version;	// 数据版本号
}
