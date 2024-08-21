package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/8/21
 */
@Data
public class JdJdbElong {

    private String id;	// 主键ID

    private String jdmc;	// 酒店名称

    private String jdmcEn;	// 酒店英文名称

    private String jdlx;	// 酒店类型id 如公寓等

    private String jddz;	// 酒店地址

    private String jddzEn;	// 酒店英文地址

    private String jddh;	// 酒店联系电话

    private String imgUrl;	// 酒店首图地址

    private Integer status;	// 酒店可用状态 0:有效 1:无效 2:删除

    private BigDecimal lonGoogle;	// 谷歌经度

    private BigDecimal latGoogle;	// 谷歌纬度

    private BigDecimal lonBaidu;	// 百度经度

    private BigDecimal latBaidu;	// 百度纬度

    private String cityId;	// 所在城市ID

    private String cityName;	// 所在城市I名称

    private String brandId;	// 酒店品牌id

    private String brandName;	// 品牌名称

    private String district;	// 行政区编码

    private String districtName;	// 行政区名称

    private String businessZone;	// 商圈编码

    private String businessZoneName;	// 商圈名称

    private Integer jdxj;	// 酒店评分 0 无星 1-5星

    private BigDecimal score;	// 酒店评分

    private String jtmc;	// 集团名称

    private Date kysj;	// 开业时间

    private Date zhzxsj;	// 最后装修时间

    private Integer ysbs;	// 映射标识 0未映射 1已映射 默认0

    private String cityId2;	// 关联城市id

    private String cityId1;	// 主城市Id

    private Date createdate;	// 创建时间

    private String mender;	// 更新人名称

    private Date savedate;	// 更新时间
}
