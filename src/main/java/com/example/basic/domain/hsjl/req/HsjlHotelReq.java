package com.example.basic.domain.hsjl.req;

import lombok.Data;

@Data
public class HsjlHotelReq {

  private String cityCode;

  /** 页码，默认为1 */
  private Integer pageNo;

  /** 入住类型：1或空-全日房，2-钟点房【即将上线】 */
  private Integer checkInType;
}
