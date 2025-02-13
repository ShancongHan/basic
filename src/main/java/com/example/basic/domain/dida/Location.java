package com.example.basic.domain.dida;

import lombok.Data;

/**
 * @author han
 * @date 2025/2/12
 */
@Data
public class Location {
    private Country country;
    private Destination destination;
    private Coordinate coordinate;
    private String stateCode;
    private String address;
}
