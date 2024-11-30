package com.example.basic.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author han
 * @date 2024/11/20
 */
@Getter
@Setter
public class EsResult {

    /**
     * 状态: 200-正常;
     */
    private Integer status;
    /**
     * 状态说明
     */
    private String message;
    /**
     * 数据
     */
    private String data;
}
