package com.example.basic.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElongHotel {

  private Long id;

  private String hotelId;

  private String hotelName;

  private String hotelNameEn;

  private String cityId;

  /**
   * 0:酒店基础数据，1:房型数据，2:图片数据，4:供应商数据，5:酒店点评、评分数据(hotel.static.grade)， 6：酒店、房型标签数据 多个用“;”隔开，以最后一次更新为主
   */
  private String modification;

  private LocalDateTime updateTime;
}
