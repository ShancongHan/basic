package com.example.basic.domain;

import lombok.Data;

/**
 * @author han
 * @date 2024/9/11
 */
@Data
public class MultiPrice {

    private String countryCode;

    private String hotelId;

    private String roomId;

    private String roomName;

    private String rateId;

    private String price;
}
