package com.example.basic.domain.hsjl.resp;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// 表示整个响应的类，对应最外层的结构
@Setter
@Getter
public class HsjlHotelResp extends HsjlResponse {

  private BussinessResponse bussinessResponse;

  @Data
  public static class BussinessResponse {

    private List<Long> hotelIds;

    /** 当前页码 */
    private Integer currentPage;

    /** 酒店总数 */
    private Integer total;

    /** 总页数 */
    private Integer totalPage;
  }
}
