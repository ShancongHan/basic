package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Location {

    /**
     * 精准定位
     */
    private LocationPoint coordinates;

    /**
     * 模糊定位
     */
    private LocationPoint obfuscated_coordinates;

    /**
     * 是否模糊定位
     */
    private boolean obfuscation_required;
}
