package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Fees {

    /**
     * 强制
     */
    private String mandatory;

    /**
     * 可选
     */
    private String optional;
}
