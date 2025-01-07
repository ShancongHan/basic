package com.example.basic.domain.eps;

import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2025/1/3
 */
@Data
public class RegionProperty {
    /**
     * id
     */
    private Long id;

    /**
     * 酒店id
     */
    private List<String> property_ids;
}
