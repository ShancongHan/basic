package com.example.basic.domain.to;

import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2024/7/31
 */
@Data
public class Descendants {

    /**
     * 国家
     */
    private List<String> country;

    /**
     * 省份、州、大区
     */
    private List<String> province_state;

    /**
     * 高等级区域
     */
    private List<String> high_level_region;

    /**
     * 联合区域
     */
    private List<String> multi_city_vicinity;

    /**
     * 城市
     */
    private List<String> city;

    /**
     * 周边
     */
    private List<String> neighborhood;

    /**
     * 机场
     */
    private List<String> airport;

    /**
     * POI
     */
    private List<String> point_of_interest;

    /**
     * 城市
     */
    private List<String> train_station;

    /**
     * 地铁
     */
    private List<String> metro_station;

    /**
     * 车站
     */
    private List<String> bus_station;
}
