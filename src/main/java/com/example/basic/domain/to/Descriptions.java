package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Descriptions {

    /**
     * Describes general building amenities at the property.
     */
    private String amenities;

    /**
     * 餐饮设施
     * Describes dining accommodations at the property.
     */
    private String dining;

    /**
     * 翻修
     * Describes any recent room or property renovations.
     */
    private String renovations;

    /**
     * 星级评定的来源
     */
    private String national_ratings;

    /**
     * 商务设施
     */
    private String business_amenities;

    /**
     * 房间
     */
    private String rooms;

    /**
     * 附近的景点/区域，通常包括与酒店的距离
     * Nearby attractions/areas, often including distances from the property.
     */
    private String attractions;

    /**
     * General location as entered by the property.
     * 酒店输入的大致位置
     */
    private String location;

    /**
     * 宣传标语
     */
    private String headline;
}
