package com.example.basic.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author han
 * @date 2024/8/21
 */
@Data
public class JdJdbMeituan {

    private Long id;	// 酒店id

    private String cityId;	// 美团城市id

    private String cityName;	// 城市名称

    private String jdmc;	// 酒店名称

    private String jdmcEn;	// 酒店英文名称

    private String jddz;	// 酒店地址

    private String jddzEn;	// 酒店英文地址

    private Integer jdxj;	// 酒店星级

    private String jddh;	// 酒店电话

    private BigDecimal lonGaode;	// 高德经度

    private BigDecimal latGaode;	// 高德维度

    private BigDecimal lonBaidu;	// 百度经度

    private BigDecimal latBaidu;	// 百度纬度

    private BigDecimal latGoogle;	// 谷歌经度

    private BigDecimal lonGoogle;	// 谷歌纬度

    private String brandId;	// 美团酒店品牌id

    private String brandName;	// 品牌名称

    private String district;	// 行政区编码

    private String districtName;	// 行政区名称

    private String businessZone;	// 商圈编码

    private String businessZoneName;	// 商圈名称

    private String jtmc;	// 集团名称

    private BigDecimal score;	// 酒店评分

    private String picUrl;	// 酒店主图地址

    private Integer hot;	// 热门程度

    private BigDecimal minPrice;	// 酒店最低价

    private Integer status;	// 酒店可用状态：0 可用 1不可用

    private Date kysj;	// 开业日期

    private Date zhzxsj;	// 最后装修日期

    private Integer ysbs;	// 映射标识 0未映射 1已映射 默认0

    private String jdlx;	// 酒店类型 多个以,隔开

    private String themeId;	// 酒店主题id 多个以,隔开

    private Integer hyzc;	// 是否含有早餐 0否 1是 默认0

    private Date createdate;	// 创建时间；

    private String mender;	// 更新人

    private Date savedate;	// 更新时间
}
