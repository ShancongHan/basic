package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/11/11
 */
@Data
public class HuazhuInfo {

    private String hotelId;	// 酒店ID

    private Integer brandCode;	// 品牌ID

    private String brandName;	// 品牌名称

    private String hotelName;	// 酒店名称

    private String hotelNameEn;	// 酒店名称英文

    private String provinceNo;	// 省ID

    private String provinceName;	// 省名称

    private String cityNo;	// 城市ID

    private String cityName;	// 城市名称

    private String cityArea;	// 区域名称

    private String hotelAddress;	// 酒店地址

    private String hotelAddressEn;	// 酒店地址英文

    private String hotelAddressTip;	// 酒店地址提示

    private String hotelTelephone;	// 电话

    private String hotelFax;	// 酒店传真

    private String zipCode;	// 邮政编码

    private Integer roomNum;	// 房间数

    private String hotelWordIntroduce;	// 酒店提示

    private String longitude;	// 酒店经度

    private String latitude;	// 酒店纬度

    private Date openingDate;	// 营业日期

    private Date trialOpeningDate;	// 试营业日期

    private String operateScope;	// 经营性质

    private Integer isOpening;	// 是否营业

    private String hotelClassification;	// 酒店类别

    private Object scaleOfTaxpayer;	// 纳税人性质

    private String contactPosition;	// 联系人职位

    private String contactName;	// 联系人姓名

    private String contactMobile;	// 联系人手机

    private String contactEmail;	// 联系人邮箱

    private String contactFax;	// 联系人传真
}
