package com.example.basic.entity;

import lombok.Data;

@Data
public class HsjlHotelInfo {

  private Long id;

  private Long hotelId;

  private String hotelName;

  private String hotelEngName;

  private String address;

  private String appearancePicUrl;

  private String telephone;

  private String fax;

  private String parentHotelGroup;

  private String parentHotelGroupName;

  private String city;

  private String cityName;

  private Double longitude;

  private Double latitude;
}
