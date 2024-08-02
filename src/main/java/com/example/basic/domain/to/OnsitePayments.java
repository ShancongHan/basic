package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class OnsitePayments {

    /**
     * 货币
     */
    private String currency;

    /**
     * 支付方式列表
     */
    private String types;
}
