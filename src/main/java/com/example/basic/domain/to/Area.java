package com.example.basic.domain.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Area {

    /**
     * 平方米
     */
    private BigDecimal square_meters;

    /**
     * 平方英尺
     */
    private BigDecimal square_feet;
}
