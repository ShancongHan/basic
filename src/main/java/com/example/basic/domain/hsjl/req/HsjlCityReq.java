package com.example.basic.domain.hsjl.req;

import lombok.Data;

@Data
public class HsjlCityReq {

  /** CN-中国（含港澳台地区） 默认查询中国所有城市 */
  private String countryCode = "CN";

  /** 是否含有起价酒店：true-只返回有起价酒店的城市，false-全量城市(不考虑是否含起价的酒店) */
  private boolean haveLowestPrice = true;
}
