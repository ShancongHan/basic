package com.example.basic.domain.elong.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class HotelResp extends ElongResponse {

  @JsonProperty("Result")
  private HotelResult result;

  @Setter
  @Getter
  public static class HotelResult {
    @JsonProperty("Count")
    private int count;

    @JsonProperty("Hotels")
    private List<Hotel> hotels;
  }

  @Setter
  @Getter
  public static class Hotel {
    @JsonProperty("HotelId")
    private String hotelId;

    @JsonProperty("HotelName")
    private String hotelName;

    @JsonProperty("HotelNameEn")
    private String hotelNameEn;

    @JsonProperty("HotelStatus")
    private int hotelStatus;

    @JsonProperty("CityId")
    private String cityId;

    @JsonProperty("Modification")
    private String modification;

    @JsonProperty("UpdateTime")
    private LocalDateTime updateTime;
  }
}
