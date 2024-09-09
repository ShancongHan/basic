package com.example.basic.domain;

import lombok.Data;

/**
 * @author han
 * @date 2024/9/6
 */
@Data
public class ExpediaPriceResult {

    private Long id;

    private String hotelId;

    /**
     * 有价
     */
    private Boolean hasPrice;
}
