package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/8/19
 */
@Data
public class JdJdb {
    private String id;	// 本地酒店编号 规则 前缀+平台酒店id

    private String interfacePlat;	// 默认平台标识，对应InterfacePlatEnum

    private String jdmc;	// 酒店名称

    private String jdmcEn;	// 酒店英文名称

    private String jdlx;	// 酒店类型id 如公寓等 多个以,隔开

    private String jddz;	// 酒店地址

    private String jddzEn;	// 酒店英文地址

    private String jddh;	// 酒店联系电话

    private String imgUrl;	// 酒店首图地址

    private Integer status;	// 酒店可用状态 0--可用，1--不可用

    private BigDecimal lonGoogle;	// 谷歌经度

    private BigDecimal latGoogle;	// 谷歌纬度

    private BigDecimal lonBaidu;	// 百度经度

    private BigDecimal latBaidu;	// 百度纬度

    private BigDecimal lonGaode;	// 高德经度

    private BigDecimal latGaode;	// 高德纬度

    private String cityId;	// 所在城市ID，对应B_CITY中的ID

    private String cityName;	// 所在城市I名称

    private String chdCityId;	// 所在子城市id

    private String chdCityName;	// 所在子城市名称

    private String brandId;	// 酒店品牌id

    private String brandName;	// 品牌名称

    private String district;	// 行政区编码

    private String districtName;	// 行政区名称

    private String businessZone;	// 商圈编码

    private String businessZoneName;	// 商圈名称

    private Integer jdxj;	// 酒店评分 0 无星 1-5星

    private BigDecimal score;	// 酒店评分

    private String jtid;	// 集团id

    private String jtmc;	// 集团名称

    private String kysj;	// 开业时间

    private String zhzxsj;	// 最后装修时间

    private Integer rank;	// 酒店推荐度 数值越大 推荐度越高

    private Date createdate;	// 创建时间

    private String mender;	// 更新人名称

    private Date savedate;	// 更新时间
}
