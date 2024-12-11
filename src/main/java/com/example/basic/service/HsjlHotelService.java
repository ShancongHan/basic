package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.HsjlApiConfig;
import com.example.basic.dao.HsjlHotelDao;
import com.example.basic.dao.HsjlHotelInfoDao;
import com.example.basic.domain.hsjl.req.HsjlHotelInfoReq;
import com.example.basic.domain.hsjl.req.HsjlHotelReq;
import com.example.basic.domain.hsjl.resp.HsjlHotelInfoResp;
import com.example.basic.domain.hsjl.resp.HsjlHotelResp;
import com.example.basic.entity.HsjlCity;
import com.example.basic.entity.HsjlHotel;
import com.example.basic.entity.HsjlHotelInfo;
import com.example.basic.utils.HsjlHttp;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HsjlHotelService {

  @Autowired private HsjlCityService hsjlCityService;

  @Autowired private HsjlHotelDao hsjlHotelDao;

  @Autowired private HsjlHotelInfoDao hsjlHotelInfoDao;

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
    int cityCount = hsjlCities.size();
    for (int i = 0; i < cityCount; i++) {
      HsjlCity hsjlCity = hsjlCities.get(i);
      try {
        int pageNo = 1;
        List<HsjlHotel> insertHotels = Lists.newArrayList();
        while (true) {
          try {
            while (callsPerMinute > maxCallsPerMinute) {
              long t2 = System.currentTimeMillis();
              long t3 = t2 - t1;
              if (t3 > 60000) {
                callsPerMinute = 0;
                t1 = t2;
                break;
              }

              System.out.println("等待中..." + t3 / 1000 + "秒");
              Thread.sleep(990);
            }

            // 执行接口调用
            HsjlHotelReq hotelReq = new HsjlHotelReq();
            hotelReq.setCityCode(hsjlCity.getCityCode());
            hotelReq.setPageNo(pageNo);
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

            if (hotelResp.getBussinessResponse().getHotelIds().size() < 1000) {
              break;
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
      } finally {
        System.out.println(
            "红色加力请求hotelList-cityCode="
                + hsjlCity.getCityCode()
                + " 第"
                + (i + 1)
                + "个城市（总数"
                + cityCount
                + "）");
      }
    }
  }

  public void syncAllHotelInfos() {

    // 酒店信息表已有的数据
    long existHotelInfoStartId = 0;
    Set<Long> existHotelInfoIdsSet = Sets.newHashSet();
    while (true) {
      List<HsjlHotelInfo> hotelInfos = hsjlHotelInfoDao.selectHotelInfoIds(existHotelInfoStartId);
      if (CollectionUtils.isEmpty(hotelInfos)) {
        break;
      }

      existHotelInfoStartId =
          hotelInfos.stream().map(HsjlHotelInfo::getId).max(Long::compareTo).get();
      hotelInfos.forEach(hotel -> existHotelInfoIdsSet.add(hotel.getHotelId()));
    }

    // 酒店id表的所有hotel_id
    long startId = 0;
    List<Long> hotelIds = Lists.newArrayList();
    while (true) {
      List<HsjlHotel> hotels = hsjlHotelDao.selectHotelIds(startId);
      if (CollectionUtils.isEmpty(hotels)) {
        break;
      }

      startId = hotels.stream().map(HsjlHotel::getId).max(Long::compareTo).get();
      hotels.forEach(
          hotel -> {
            if (!existHotelInfoIdsSet.contains(hotel.getHotelId())) {
              hotelIds.add(hotel.getHotelId());
            }
          });
    }

    int maxCallsPerMinute = 99;
    int callsPerMinute = 0;
    long t1 = System.currentTimeMillis();
    List<List<Long>> subList = Lists.partition(hotelIds, 10);
    int hotelCount = subList.size();
    for (int i = 0; i < hotelCount; i++) {
      List<Long> subHotelIds = subList.get(i);
      try {
        List<HsjlHotelInfo> insertHotelInfos = Lists.newArrayList();
        while (callsPerMinute > maxCallsPerMinute) {
          long t2 = System.currentTimeMillis();
          long t3 = t2 - t1;
          if (t3 > 60000) {
            callsPerMinute = 0;
            t1 = t2;
            break;
          }

          System.out.println("等待中..." + t3 / 1000 + "秒");
          Thread.sleep(990);
        }

        // 执行接口调用
        HsjlHotelInfoReq hotelInfoReq = new HsjlHotelInfoReq();
        hotelInfoReq.setHotelIds(subHotelIds);
        HsjlHotelInfoResp hotelInfosResp = this.getHotelInfos(hotelInfoReq);
        callsPerMinute++;
        if (Objects.isNull(hotelInfosResp)
            || Objects.isNull(hotelInfosResp.getBussinessResponse())
            || CollectionUtils.isEmpty(hotelInfosResp.getBussinessResponse().getHotelInfos())) {
          continue;
        }

        for (HsjlHotelInfoResp.HotelInfo hotelInfo :
            hotelInfosResp.getBussinessResponse().getHotelInfos()) {
          HsjlHotelInfo hsjlHotelInfo = new HsjlHotelInfo();
          BeanUtils.copyProperties(hotelInfo, hsjlHotelInfo);
          insertHotelInfos.add(hsjlHotelInfo);
        }

        if (CollectionUtils.isNotEmpty(insertHotelInfos)) {
          hsjlHotelInfoDao.saveBatch(insertHotelInfos);
        }
      } catch (Exception e) {
        System.err.println("红色加力请求hotelInfos-ids=" + subHotelIds + "，异常：" + e);
      } finally {
        System.out.println(
            "红色加力请求hotelInfos-ids="
                + subHotelIds
                + " 第"
                + (i + 1)
                + "组酒店（总数"
                + hotelIds.size()
                + "）");
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

  public HsjlHotelInfoResp getHotelInfos(HsjlHotelInfoReq request) {
    Map<String, Object> businessRequest = this.buildHotelInfoBusinessRequest(request);
    String responseBody =
        HsjlHttp.httpRequest(HsjlApiConfig.REQUEST_TYPE_HOTEL_INFO, businessRequest);
    if (StringUtils.isEmpty(responseBody)) {
      return null;
    }
    return JSON.parseObject(responseBody, HsjlHotelInfoResp.class);
  }

  private Map<String, Object> buildHotelInfoBusinessRequest(HsjlHotelInfoReq request) {
    Map<String, Object> businessRequest = Maps.newHashMap();
    businessRequest.put("hotelIds", request.getHotelIds());
    if (CollectionUtils.isNotEmpty(request.getSettings())) {
      businessRequest.put("settings", request.getSettings());
    }
    return businessRequest;
  }
}
