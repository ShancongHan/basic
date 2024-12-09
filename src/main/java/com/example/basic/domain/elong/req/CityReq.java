package com.example.basic.domain.elong.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CityReq {

  @JsonProperty("CountryType")
  private int countryType;

  @JsonProperty("CityIdType")
  private int cityIdType;

  @JsonProperty("IsNeedLocation")
  private boolean isNeedLocation;

  @JsonProperty("PageSize")
  private int pageSize;

  @JsonProperty("PageIndex")
  private int pageIndex;
}
