package com.example.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author han
 * @date 2024/12/26
 */
@Data
public class ExpediaStatistics {
    private Long id;	// 主键id

    private String hotelId;	// 酒店id

    private Integer statisticsId;	// 统计id

    private String statisticsNameEn;	// 统计名称(英文)

    private String statisticsName;	// 统计名称

    private String statisticsValue;	// 统计值

    private Date createTime;	// 系统新增时间

    private Date updateTime;	// 系统更新时间
}
