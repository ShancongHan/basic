package com.example.basic.domain;

import com.example.basic.domain.to.Ancestors;
import com.example.basic.domain.to.Associations;
import com.example.basic.domain.to.Coordinates;
import com.example.basic.domain.to.Descendants;
import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2024/7/31
 */
@Data
public class Region {

    /**
     * id
     */
    private String id;

    /**
     * region 类型
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 全名称
     */
    private String name_full;

    /**
     * 国家二字码
     */
    private String country_code;

    /**
     * 国家分区代码
     */
    private String country_subdivision_code;

    /**
     * 坐标
     */
    private Coordinates coordinates;

    /**
     * 关联点
     */
    private Associations associations;

    /**
     * 上级节点
     */
    private List<Ancestors> ancestors;

    /**
     * 下级节点
     */
    private Descendants descendants;

    /**
     * Include the list of property IDs within the bounding polygon of each region
     * 包含每个区域边界多边形内的房产 ID 列表
     */
    private List<String> property_ids;

    /**
     * Include the list of property IDs within the bounding polygon of each region and property IDs from the surrounding area if minimal properties are within the region.
     * 包括每个区域边界多边形内的房产 ID 列表，如果该区域内的房产最少，则还包括周边区域的房产 ID。
     */
    private List<String> property_ids_expanded;

    /**
     * 分类
     */
    private List<String> categories;

    /**
     * 标签
     */
    private List<String> tags;
}
