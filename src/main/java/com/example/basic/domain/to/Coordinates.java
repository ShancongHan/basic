package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/7/31
 */
@Data
public class Coordinates {

    /**
     * 中心经度
     */
    private double center_longitude;

    /**
     * 中心纬度
     */
    private double center_latitude;

    /**
     * 边界多边形
     */
    private String bounding_polygon;
}
