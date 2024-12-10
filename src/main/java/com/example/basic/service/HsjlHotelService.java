package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.HsjlApiConfig;
import com.example.basic.dao.HsjlHotelDao;
import com.example.basic.domain.hsjl.req.HsjlHotelReq;
import com.example.basic.domain.hsjl.resp.HsjlHotelResp;
import com.example.basic.entity.HsjlCity;
import com.example.basic.entity.HsjlHotel;
import com.example.basic.utils.HsjlHttp;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HsjlHotelService {

  @Autowired private HsjlCityService hsjlCityService;

  @Autowired private HsjlHotelDao hsjlHotelDao;

  public void syncAllHotelList() {
    List<HsjlCity> hsjlCities = hsjlCityService.selectCityList();

    long startId = 0;
    Set<Long> existHotelIds = Sets.newHashSet();
    while (true) {
      List<HsjlHotel> hotels = hsjlHotelDao.selectHotelIds(startId);
      if (CollectionUtils.isEmpty(hotels)) {
        break;
      }

      startId = hotels.stream().map(HsjlHotel::getId).max(Long::compareTo).get();
      hotels.forEach(hotel -> existHotelIds.add(hotel.getHotelId()));
    }

    int totalHotels = 0;
    int maxCallsPerMinute = 99;
    int callsPerMinute = 0;
    long t1 = System.currentTimeMillis();
    for (HsjlCity hsjlCity : hsjlCities) {
      try {
        int pageNo = 1;
        List<HsjlHotel> insertHotels = Lists.newArrayList();
        while (true) {
          HsjlHotelReq hotelReq = new HsjlHotelReq();
          hotelReq.setCityCode(hsjlCity.getCityCode());
          hotelReq.setPageNo(pageNo);

          try {
            while (callsPerMinute > maxCallsPerMinute) {
              long t2 = System.currentTimeMillis();
              if (t2 - t1 > 60000) {
                callsPerMinute = 0;
                t1 = t2;
                break;
              }

              System.out.println("红色加力请求hotelList限制1分钟100个请求量，等待中...");
              Thread.sleep(800);
            }

            // 执行接口调用
            HsjlHotelResp hotelResp = this.getHotelIds(hotelReq);
            callsPerMinute++;
            if (Objects.isNull(hotelResp)
                || Objects.isNull(hotelResp.getBussinessResponse())
                || CollectionUtils.isEmpty(hotelResp.getBussinessResponse().getHotelIds())) {
              break;
            }

            for (Long hotelId : hotelResp.getBussinessResponse().getHotelIds()) {
              HsjlHotel hsjlHotel = new HsjlHotel();
              hsjlHotel.setHotelId(hotelId);
              hsjlHotel.setCityCode(hsjlCity.getCityCode());
              hsjlHotel.setCityName(hsjlCity.getCityName());
              if (!existHotelIds.contains(hotelId)) {
                insertHotels.add(hsjlHotel);
              }
            }
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("红色加力请求hotelList异常：" + e);
          } finally {
            pageNo++;
          }
        }

        if (CollectionUtils.isNotEmpty(insertHotels)) {
          List<List<HsjlHotel>> subList = Lists.partition(insertHotels, 1000);
          for (List<HsjlHotel> hotels1 : subList) {
            hsjlHotelDao.saveBatch(hotels1);
          }
          totalHotels += insertHotels.size();
          System.out.println(
              "红色加力请求hotelList-cityCode=" + hsjlCity.getCityCode() + "，已执行酒店数量=" + totalHotels);
        }
      } catch (Exception e) {
        System.err.println("红色加力请求hotelList-cityCode=" + hsjlCity.getCityCode() + "，异常：" + e);
      }
    }
  }

  public HsjlHotelResp getHotelIds(HsjlHotelReq request) {
    Map<String, Object> businessRequest = this.buildBusinessRequest(request);
    String responseBody =
        HsjlHttp.httpRequest(HsjlApiConfig.REQUEST_TYPE_HOTEL_LIST, businessRequest);
    if (StringUtils.isEmpty(responseBody)) {
      return null;
    }
    return JSON.parseObject(responseBody, HsjlHotelResp.class);
  }

  private Map<String, Object> buildBusinessRequest(HsjlHotelReq request) {
    Map<String, Object> businessRequest = Maps.newHashMap();
    businessRequest.put("cityCode", request.getCityCode());
    businessRequest.put("checkInType", request.getCheckInType());
    businessRequest.put("pageNo", request.getPageNo());
    return businessRequest;
  }
}
