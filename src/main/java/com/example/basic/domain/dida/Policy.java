package com.example.basic.domain.dida;

import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2025/2/12
 */
@Data
public class Policy {
    private String description;
    private String checkinFrom;
    private String checkoutTo;
    private String importantNotice;
    private List<ExtraInfo> extraInfoList;
}
