package com.example.basic.domain.elong.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CityResp extends ElongResponse {

  @JsonProperty("Result")
  private Result result;

  @Setter
  @Getter
  public static class Result {
    @JsonProperty("Citys")
    private List<City> citys;
  }

  @Setter
  @Getter
  public static class City {
    @JsonProperty("CityId")
    private String cityId;

    @JsonProperty("CityName")
    private String cityName;

    @JsonProperty("CityNameEn")
    private String cityNameEn;

    @JsonProperty("ProvinceId")
    private String provinceId;

    @JsonProperty("ProvinceName")
    private String provinceName;

    @JsonProperty("ProvinceNameEn")
    private String provinceNameEn;

    @JsonProperty("CountryId")
    private int countryId;

    @JsonProperty("CountryCode")
    private String countryCode;

    @JsonProperty("CountryName")
    private String countryName;

    @JsonProperty("CountryNameEn")
    private String countryNameEn;

    @JsonProperty("Locations")
    private Object locations; // 使用 Object 类型来处理可能是整数或列表的情况

    @JsonProperty("filterType")
    private int filterType;

    @JsonProperty("filterId")
    private int filterId;

    @JsonProperty("sugActInfo")
    private String sugActInfo;
  }
}
