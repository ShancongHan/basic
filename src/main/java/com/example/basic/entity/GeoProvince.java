package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/5/10
 */
@Data
public class GeoProvince {

    private Integer id;	// 主键id

    private String provinceName;	// 省份中文名称

    private String provinceFullName;	// 省份中文名全称 例：广东省、西藏自治区

    private String provinceEname;	// 省份英文名称

    private String nation;	// 国家id

    private String nationName;	// 国家名称

    private Integer sn;	// 序号

    private String remark;	// 备注信息

    private String creator;	// 创建人名称

    private Date createdate;	// 创建时间

    private String mender;	// 更新者名称

    private Date savedate;	// 更新时间

    private Integer delFlag;	// 删除标记（0：正常；1：删除）
}
