package com.example.basic.domain.elong.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelInfoResp extends ElongResponse {

  @JsonProperty("Result")
  private HotelInfoResult result;

  @Setter
  @Getter
  public static class HotelInfoResult {

    @JsonProperty("Detail")
    private HotelDetail hotelDetail;
  }

  @Setter
  @Getter
  public static class HotelDetail {
    @JsonProperty("HotelId")
    private String hotelId;

    @JsonProperty("HotelName")
    private String hotelName;

    @JsonProperty("HotelNameEn")
    private String hotelNameEn;

    @JsonProperty("HotelStatus")
    private int hotelStatus;

    @JsonProperty("Address")
    private String Address;

    @JsonProperty("AddressEn")
    private String AddressEn;

    @JsonProperty("Phone")
    private String Phone;

    @JsonProperty("GoogleLat")
    private BigDecimal googleLat;

    @JsonProperty("GoogleLon")
    private BigDecimal googleLon;

    @JsonProperty("BaiduLat")
    private BigDecimal baiduLat;

    @JsonProperty("BaiduLon")
    private BigDecimal baiduLon;

    @JsonProperty("CityId")
    private String cityId;

    @JsonProperty("CityName")
    private String cityName;
  }
}
