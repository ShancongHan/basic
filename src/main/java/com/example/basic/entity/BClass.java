package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/5/10
 */
@Data
public class BClass {

    private String id;	// ID主键

    private String cName;	// 名称

    private String engName;	// 英文名称

    private Integer sn;	// 顺序号

    private String parNo;	// 上级ID

    private String typeNo;	// 类别

    private String by1;	// 备用1

    private String by2;	// 备用2

    private String by3;	// 备用3

    private String by4;	// 备用4

    private String by5;	// 备用5

    private String by6;	// 备用6

    private String cuId;	// 创建者创建者

    private String creator;	// 创建者名称

    private Date createdate;	//

    private String muId;	// 更新者更新者

    private String mender;	// 更新者名称

    private Date savedate;	// 更新时间更新时间

    private String remark;	// 备注信息

    private Integer delFlag;	// 删除标记删除标记（0：正常；1：删除）

    private Integer version;	// 数据版本号
}
