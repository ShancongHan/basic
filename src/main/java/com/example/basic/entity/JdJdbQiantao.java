package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/8/21
 */
@Data
public class JdJdbQiantao {
    private Long id;	// id

    private String jdid;	// 千桃酒店id

    private String cityId;	// 千桃城市id

    private String jdmc;	// 酒店名称

    private String jddz;	// 酒店地址

    private Integer jdxj;	// 酒店星级

    private String jddh;	// 酒店电话

    private String openingTime;	// 开业时间

    private String fixTime;	// 装修时间

    private BigDecimal lonWgsBd;	// 百度经度

    private BigDecimal latWgsBd;	// 百度维度

    private BigDecimal lonWgsGd;	// 高德经度

    private BigDecimal latWgsGd;	// 高德维度

    private String brandCode;	// 品牌名称

    private String districtName;	// 行政区名称

    private String businessZoneName;	// 商圈名称

    private Integer status;	// 酒店状态 1可用 2删除

    private String localJdid;	// 本地酒店id，冗余映射本地id

    private String picUrl;	// 酒店主图地址

    private Integer ysbs;	// 映射标识 0未映射 1已映射 默认0

    private Date savedate;	// 更新时间
}
