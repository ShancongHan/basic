package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.ElongApiConfig;
import com.example.basic.dao.ElongHotelDao;
import com.example.basic.domain.elong.req.HotelInfoReq;
import com.example.basic.domain.elong.req.HotelReq;
import com.example.basic.domain.elong.resp.HotelInfoResp;
import com.example.basic.domain.elong.resp.HotelResp;
import com.example.basic.entity.ElongCity;
import com.example.basic.entity.ElongHotel;
import com.example.basic.utils.ElongHttp;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ElongHotelService {

  @Autowired private ElongCityService elongCityService;

  @Autowired private ElongHotelDao elongHotelDao;

  public void syncAllHotel() {
    List<ElongCity> cityList = elongCityService.selectCityList();
    for (ElongCity elongCity : cityList) {
      int pageIndex = 1;
      int pageSize = 2000;
      int saveTotalCount = 0;
      while (true) {
        try {
          HotelReq req = new HotelReq();
          req.setCityId(elongCity.getCityId());
          req.setPageIndex(pageIndex);
          req.setPageSize(pageSize);
          HotelResp hotelResp = this.getHotelList(req);
          if (hotelResp == null
              || hotelResp.getResult() == null
              || CollectionUtils.isEmpty(hotelResp.getResult().getHotels())) {
            break;
          }

          List<ElongHotel> elongHotels = Lists.newArrayList();
          for (HotelResp.Hotel hotel : hotelResp.getResult().getHotels()) {
            if (Objects.nonNull(hotel.getHotelStatus()) && hotel.getHotelStatus() == 1) {
              ElongHotel elongHotel = new ElongHotel();
              BeanUtils.copyProperties(hotel, elongHotel);
              elongHotels.add(elongHotel);
            }
          }

          if (CollectionUtils.isNotEmpty(elongHotels)) {
            elongHotelDao.saveBatch(elongHotels);
            saveTotalCount += elongHotels.size();
            System.out.println("艺龙酒店同步-已保存hotel数量->" + saveTotalCount);
          }
        } catch (Exception e) {
          System.err.println(
              "艺龙酒店同步-请求hotelList->elongCityId=" + elongCity.getCityId() + "，异常：" + e);
        } finally {
          pageIndex++;
        }
      }

      System.out.println("艺龙酒店同步结束-已保存hotel数量->" + saveTotalCount);
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
