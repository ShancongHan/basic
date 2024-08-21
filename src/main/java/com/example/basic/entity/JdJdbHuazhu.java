package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/8/21
 */
@Data
public class JdJdbHuazhu {

    private Long id;	// id

    private String jdid;	// 华住酒店id

    private String cityId;	// 华住城市id

    private String jdmc;	// 酒店名称

    private String jddz;	// 酒店地址

    private Integer jdxj;	// 酒店星级

    private String jddh;	// 酒店电话

    private BigDecimal lonWgs;	// 高德经度

    private BigDecimal latWgs;	// 高德维度

    private String brandId;	// 华住酒店品牌id

    private String brandName;	// 品牌名称

    private Integer status;	// 1 可用 2不可用

    private String picUrl;	// 酒店主图地址

    private Integer ysbs;	// 映射标识 0未映射 1已映射 默认0

    private Date savedate;	// 更新时间
}
