package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/8/21
 */
@Data
public class JdJdbDongcheng {

    private Long id;	// id

    private String jdid;	// 东呈酒店id

    private String cityId;	// 东呈城市id

    private String jdmc;	// 酒店名称

    private String jddz;	// 酒店地址

    private String jddh;	// 酒店电话

    private BigDecimal lonWgs;	// 经度

    private BigDecimal latWgs;	// 维度

    private String brandName;	// 品牌名称

    private String description;	// 简介

    private Integer breakfast;	// 是否含早 1 是 0否

    private String picUrl;	// 酒店主图地址

    private Integer ysbs;	// 映射标识 0未映射 1已映射 默认0

    private Integer status;	// 1 可预订 2不可预订

    private Date savedate;	// 更新时间
}
