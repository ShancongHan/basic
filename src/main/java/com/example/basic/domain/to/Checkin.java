package com.example.basic.domain.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Checkin {

    /**
     * 有值即24小时营业
     */
    @JsonProperty(value = "24_hour")
    private String _24hour;

    /**
     * 开始时间
     */
    private String begin_time;

    /**
     * 结束时间
     */
    private String end_time;

    /**
     * checkIn 入住政策
     */
    private String instructions;

    /**
     * 任何特别说明
     */
    private String special_instructions;

    /**
     * 最小年龄
     */
    private int min_age;

}
