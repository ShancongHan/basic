package com.example.basic.domain.dida;

import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2025/2/12
 */

@Data
public class HotelResponse {
    private String traceId;
    private String timestamp;
    private List<DidaHotelInfoResult> data;
}
