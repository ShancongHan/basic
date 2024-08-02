package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Address {

    /**
     * 长地址第一行
     */
    private String line_1;

    /**
     * 长地址第而行(不一定有值)
     */
    private String line_2;

    /**
     * 城市名字
     */
    private String city;

    /**
     * province_code
     */
    private String state_province_code;

    /**
     * province_name
     */
    private String state_province_name;

    /**
     * 邮政编码
     */
    private String postal_code;

    /**
     * 国家二字码
     */
    private String country_code;

    /**
     * 需要混淆?
     * 标记以指示在预订房产之前是否需要对房产地址进行混淆。如果为真，则只能向客户显示该地址的城市、省份和国家/地区。
     * Flag to indicate whether a property address requires obfuscation before the property has been booked. If true, only the city, province, and country of the address can be shown to the customer.
     */
    private boolean obfuscation_required;

    /**
     * 本土化
     */
    private String localized;
}
