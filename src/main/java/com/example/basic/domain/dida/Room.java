package com.example.basic.domain.dida;

import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2025/2/12
 */
@Data
public class Room {
    private int id;
    private String name;
    private Boolean hasWifi;
    private Boolean hasWindow;
    private Integer maxOccupancy;
    private String floor;
    private List<Images> images;
    private String size; // Optional field
}
