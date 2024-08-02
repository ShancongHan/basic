package com.example.basic.domain.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class LocationPoint {

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;
}
