package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/11/30
 */
@Data
public class WstHotelChargePointPolicyDictionary {

    private String type;	// 政策类型

    private String name;	// 政策类型名称

    private String value;	// 值

    private String valueExplain;	// 值含义

    private String descriptions; //值及值说明(JSON串)
}
