package com.example.basic.domain.expedia;

import lombok.Data;

/**
 * @author han
 * @date 2024/12/31
 */
@Data
public class BatchUpdateResult {

    /**
     * 更新状态，true-成功；flase-失败
     */
    private Boolean status;

    /**
     * 失败的酒店id
     */
    private String hotelId;
}
