package com.example.basic.domain.elong.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelInfoReq {

  @JsonProperty("HotelId")
  private String hotelId;

  /**
   * 英文逗号分割 1、Detail
   *
   * <p>2、Suppliers
   *
   * <p>3、Rooms
   *
   * <p>4、Images
   *
   * <p>5、TelList
   *
   * <p>6、HotelTags、RoomTags
   */
  @JsonProperty("Options")
  private String options;
}