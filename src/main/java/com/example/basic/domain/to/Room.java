package com.example.basic.domain.to;

import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Room {

    /**
     * roomId
     */
    private String id;

    /**
     * 房间名称
     */
    private String name;

    /**
     * 房间描述
     */
    private String descriptions;

    /**
     * 房间设施
     */
    private String amenities;

    /**
     * 房间图片
     */
    private List<Images> images;

    /**
     * 床型
     */
    private String bed_groups;

    /**
     * 面积
     */
    private Area area;

    /**
     * 入住信息
     */
    private Occupancy occupancy;
}
