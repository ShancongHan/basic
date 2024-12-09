package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.ElongApiConfig;
import com.example.basic.domain.elong.req.CityReq;
import com.example.basic.domain.elong.req.HotelInfoReq;
import com.example.basic.domain.elong.req.HotelReq;
import com.example.basic.domain.elong.resp.CityResp;
import com.example.basic.domain.elong.resp.HotelInfoResp;
import com.example.basic.domain.elong.resp.HotelResp;
import com.example.basic.utils.ElongHttp;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElongHotelService {

  @Autowired private ElongCityService elongCityService;

  public void syncAllHotel() {
    int countryType = 1;
    int cityIdType = 1;
    int pageIndex = 1;
    int pageSize = 200;
    int hotelPageSize = 2000;
    while (true) {
      try {
        CityReq req = new CityReq();
        req.setCountryType(countryType);
        req.setCityIdType(cityIdType);
        req.setPageIndex(pageIndex);
        req.setPageSize(pageSize);
        CityResp cityResp = elongCityService.getCityList(req);
        if (cityResp == null
            || cityResp.getResult() == null
            || CollectionUtils.isEmpty(cityResp.getResult().getCitys())) {
          break;
        }

        int hotelPageIndex = 1;
        for (CityResp.City city : cityResp
                        .getResult()
                        .getCitys()) {
          try {
          String cityId = city.getCityId();
          HotelReq hotelReq = new HotelReq();
          hotelReq.setCityId(cityId);
          hotelReq.setPageIndex(hotelPageIndex);
          hotelReq.setPageSize(hotelPageSize);
          HotelResp hotelList = this.getHotelList(hotelReq);

          } catch (Exception e) {
            System.err.println("请求hotelList异常：" + e);
          } finally {
            hotelPageIndex++;
          }
        }
      } catch (Exception e) {
        System.err.println("请求cityList异常：" + e);
      } finally {
        pageIndex++;
      }
    }
  }

  public HotelResp getHotelList(HotelReq request) {
    String data = buildListData(request);
    String responseBody = ElongHttp.httpRequest(ElongApiConfig.URL_HOTEL_LIST, data);
    if (StringUtils.isEmpty(responseBody)) {
      return null;
    }
    return JSON.parseObject(responseBody, HotelResp.class);
  }

  public HotelInfoResp getHotelInfo(HotelInfoReq request) {
    String data = buildInfoData(request);
    String responseBody = ElongHttp.httpRequest(ElongApiConfig.URL_HOTEL_INFO, data);
    if (StringUtils.isEmpty(responseBody)) {
      return null;
    }
    return JSON.parseObject(responseBody, HotelInfoResp.class);
  }

  private String buildListData(HotelReq request) {
    StringBuilder dataBuilder = new StringBuilder();
    dataBuilder.append("{\"Version\":\"").append(ElongApiConfig.VERSION).append("\",");
    dataBuilder.append("\"Local\":\"").append(ElongApiConfig.LOCAL).append("\",");
    dataBuilder.append("\"Request\":{");
    if (request.getStartTime() != null) {
      dataBuilder.append("\"StartTime\":\"").append(request.getStartTime()).append("\",");
    }
    if (request.getEndTime() != null) {
      dataBuilder.append("\"EndTime\":\"").append(request.getEndTime()).append("\",");
    }
    dataBuilder.append("\"CityId\":\"").append(request.getCityId()).append("\",");
    dataBuilder.append("\"PageSize\":\"").append(request.getPageSize()).append("\",");
    dataBuilder.append("\"PageIndex\":\"").append(request.getPageIndex()).append("\"");
    dataBuilder.append("}}");
    return dataBuilder.toString();
  }

  private String buildInfoData(HotelInfoReq request) {
    return "{\"Version\":\""
        + ElongApiConfig.VERSION
        + "\","
        + "\"Local\":\""
        + ElongApiConfig.LOCAL
        + "\","
        + "\"Request\":{"
        + "\"HotelId\":\""
        + request.getHotelId()
        + "\","
        + "\"Options\":\""
        + request.getOptions()
        + "\""
        + "}}";
  }
}
