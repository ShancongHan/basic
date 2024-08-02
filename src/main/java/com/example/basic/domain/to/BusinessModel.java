package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class BusinessModel {

    /**
     * expedia
     */
    private boolean expedia_collect;

    /**
     * 酒店是否可以在客人抵达时收取该酒店的款项。
     */
    private boolean property_collect;
}
