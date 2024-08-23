package com.example.basic.domain;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/22
 */
@Data
public class DongChengCity {

    private Integer cid;//城市id eg: 782,

    private String name;//城市名称  eg: 蒲种,

    private String description;// eg: null,

    private String pinyin;// 城市拼音 eg: puzhong,

    private String jianpin;//简拼 eg: puz,

    private String type;// eg: 0,

    private String parent_id;// eg: 0,

    private String ordernum;// eg: 0,

    private String ProvinceID;// eg: 35,

    private Integer CityID;// eg: 350,

    private String AreaID;// eg: null
}
