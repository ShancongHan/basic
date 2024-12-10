package com.example.basic.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ElongHotelInfo {

  private String hotelId;

  private String hotelName;

  private String hotelNameEn;

  private Integer hotelStatus;

  private String address;

  private String addressEn;

  private String phone;

  private BigDecimal googleLat;

  private BigDecimal googleLon;

  private BigDecimal baiduLat;

  private BigDecimal baiduLon;

  private String cityId;

  private String cityName;
}
