package com.example.basic.domain.hsjl.resp;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// 表示整个响应的类，对应最外层的结构
@Setter
@Getter
public class HsjlHotelInfoResp extends HsjlResponse {

  private BussinessResponse bussinessResponse;

  @Data
  public static class BussinessResponse {

    private List<HotelInfo> hotelInfos;
  }

  @Data
  public static class HotelInfo {

    private Long hotelId;

    private String hotelName;

    private String hotelEngName;

    private String address;

    private String appearancePicUrl;

    private String telephone;

    private String fax;

    private String parentHotelGroup;

    private String parentHotelGroupName;

    private String city;

    private String cityName;

    private Double longitude;

    private Double latitude;
  }
}
