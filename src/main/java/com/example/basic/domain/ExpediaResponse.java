package com.example.basic.domain;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author han
 * @date 2024/7/31
 */
@Data
@Accessors(chain = true)
@Builder
public class ExpediaResponse {

    private Integer total;
    private String nextPageUrl;
    private Integer load;
    private String body;
}
