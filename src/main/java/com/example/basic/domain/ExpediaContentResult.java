package com.example.basic.domain;

import lombok.Data;

/**
 * @author han
 * @date 2024/9/5
 */
@Data
public class ExpediaContentResult {

    private Long id;

    private String hotelId;

    /**
     * 真实存在
     */
    private Boolean realExist;

    /**
     * 传真
     */
    private String fax;

    /**
     * 主题
     */
    private String themes;

    /**
     * 支持语种
     */
    private String spokenLanguages;

    /**
     * 主图
     */
    private String heroImageMiddle;

    /**
     * 有中文信息
     */
    private Boolean hasZh;

    /**
     * 中文名
     */
    private String name;

    /**
     * 中文地址
     */
    private String address;
}
