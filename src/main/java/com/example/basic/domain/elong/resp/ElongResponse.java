package com.example.basic.domain.elong.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ElongResponse {

  @JsonProperty("Code")
  private String code;

  @JsonProperty("Guid")
  private String guid;

  private String errMsg;
}
