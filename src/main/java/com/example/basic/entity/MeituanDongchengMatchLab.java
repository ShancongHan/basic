package com.example.basic.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/11/11
 */
@Data
public class MeituanDongchengMatchLab {

    @ExcelProperty(value = "ID")
    private Long id;	// id

    @ExcelProperty(value = "美团ID")
    private Long mtId;	// 道旅酒店id

    @ExcelProperty(value = "美团名称")
    private String mtName;	// 道旅酒店名称

    @ExcelProperty(value = "东呈名字")
    private String dongchengName;	// 东呈酒店名称

    @ExcelProperty(value = "美团地址")
    private String mtAddress;	// expedia地址

    @ExcelProperty(value = "东呈地址")
    private String dongchengAddress;	// daolv地址

    @ExcelProperty(value = "距离")
    private BigDecimal diffMeter;	// 相差距离

    @ExcelProperty(value = "美团电话")
    private String mtTel;	// expedia手机号

    @ExcelProperty(value = "东呈电话")
    private String dongchengTel;	// daolv手机号

    @ExcelProperty(value = "得分")
    private Integer score;	// 匹配得分

    @ExcelProperty(value = "匹配到多个")
    private Boolean multiMatch;	// 匹配到多个

    @ExcelProperty(value = "东呈ID")
    private String dongchengId;	// 东呈酒店Id

    private BigDecimal mtLatitude;	// 美团纬度

    private BigDecimal mtLongitude;	// 美团经度

    private BigDecimal dongchengLatitude;	// 东呈纬度

    private BigDecimal dongchengLongitude;	// 东呈经度

    private Integer diffLevel;	// 距离等级

    private Integer nameMatch;	// 酒店名称是否匹配

    private Integer addressMatch;	// 酒店地址是否匹配

    private Integer telMatch;	// 酒店电话是否匹配

    private Boolean match;	// 最终匹配
}
