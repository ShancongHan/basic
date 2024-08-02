package com.example.basic.domain;

import com.example.basic.domain.to.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Content {

    /**
     * id
     */
    private String property_id;

    /**
     * 酒店名字
     */
    private String name;

    /**
     * 地址
     */
    private Address address;

    /**
     * 多种类型的评级
     */
    private Ratings ratings;

    /**
     * 定位
     */
    private Location location;

    /**
     * 电话
     */
    private String phone;

    /**
     * 传真
     */
    private String fax;

    /**
     * 分类
     */
    private String category;

    /**
     * 排名
     * 根据 EPS 交易数据和房产详情对房产进行排序
     */
    private int rank;

    /**
     * 业务模式
     * How and when the payment can be taken.
     */
    private BusinessModel business_model;

    /**
     * checkin
     */
    private Checkin checkin;

    /**
     * checkout
     */
    private Checkout checkout;

    /**
     * Information related to a property's fees.
     */
    private Fees fees;

    /**
     * 酒店政策
     */
    private Policies policies;

    /**
     * 酒店属性
     */
    private Attributes attributes;

    /**
     * 设施
     */
    private String amenities;

    /**
     * 酒店图片
     */
    private List<Images> images;

    /**
     * 现付方式
     */
    private OnsitePayments onsite_payments;

    /**
     * 房型列表
     */
    private Map<String, Room> rooms;

    /**
     * 房价ID列表
     */
    private String rates;

    /**
     * 日期
     */
    private HotelDates dates;

    /**
     * 描述
     */
    private Descriptions descriptions;

    /**
     * 房产的统计数据，例如楼层数。请参阅我们的统计数据参考，了解当前已知的统计数据 ID 和名称值
     * Statistics of the property, such as number of floors. See our statistics reference for current known statistics ID and name values.
     * {"52":{"id":"52","name":"Total number of rooms - 820","value":"820"},"54":{"id":"54","name":"Number of floors - 38","value":"38"}}
     */
    private String statistics;

    /**
     * 机场
     * {"preferred":{"iata_airport_code":"SGF"}}
     */
    private String airports;

    /**
     * 主题
     * {"2337":{"id":"2337","name":"Luxury Hotel"},"2341":{"id":"2341","name":"Spa Hotel"}}
     */
    private String themes;

    /**
     * 全包服务说明
     */
    private AllInclusive all_inclusive;

    /**
     * Tax ID.
     */
    private String tax_id;

    /**
     * 连锁
     * {"id":-6,"name":"Hyatt Hotels"}
     * {"id":-6,"name":"Hyatt Hotels","brands":"[]"}
     */
    private String chain;

    /**
     * 品牌
     * {"id":"2209","name":"Hyatt Place"}
     */
    private String brand;

    /**
     * 支持语言
     */
    private String spoken_languages;

    /**
     * 多个酒店物业???
     * Boolean value indicating if a property is a multi-unit property.
     */
    private boolean multi_unit;

    /**
     * 付款注册推荐支持
     * Boolean value indicating if a property may require payment registration to process payments, even when using the property_collect Business Model. If true, then a property may not be successfully bookable without registering payments first.
     * 布尔值，表示即使使用 property_collect 业务模型，酒店是否也需要付款登记才能处理付款。如果为 true，则酒店可能无法在未先登记付款的情况下成功预订。
     */
    private boolean payment_registration_recommended;

    /**
     * 供应商-expedia
     */
    private String supply_source;
}
