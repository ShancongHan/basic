package com.example.basic.domain.dida;

import lombok.Data;

import java.util.List;

/**
 * @author han
 * @date 2025/2/12
 */
@Data
public class DidaHotelInfoResult {
    private String language;
    private int id;
    private String name;
    private Location location;
    private String telephone;
    private int starRating;
    private String zipCode;
    private String airportCode;
    private Category category;
    private String description;
    private Policy policy;
    private List<Facility> facilities;
    private List<Images> images;
    private List<Room> rooms;
    private List<String> giataCodes;
}
