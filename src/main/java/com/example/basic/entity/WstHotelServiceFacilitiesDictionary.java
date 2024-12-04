package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/11/30
 */
@Data
public class WstHotelServiceFacilitiesDictionary {
    private String id;	// 属性id

    private String name;	// 属性名称
    private String type;	// 类型

    private String descriptions;	// 值及值说明(JSON串)格式如{"0":"酒店内","1":"酒店附近"}

    private String remark;	// 备注

}
