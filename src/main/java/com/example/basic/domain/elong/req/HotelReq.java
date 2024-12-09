package com.example.basic.domain.elong.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class HotelReq {

  @JsonProperty("StartTime")
  private LocalDateTime startTime;

  @JsonProperty("EndTime")
  private LocalDateTime endTime;

  @JsonProperty("CityId")
  private String cityId;

  @JsonProperty("PageSize")
  private int pageSize;

  @JsonProperty("PageIndex")
  private int pageIndex;
}
