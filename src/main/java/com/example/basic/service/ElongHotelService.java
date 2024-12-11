package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.ElongApiConfig;
import com.example.basic.dao.ElongHotelDao;
import com.example.basic.dao.ElongHotelInfoDao;
import com.example.basic.domain.elong.req.HotelInfoReq;
import com.example.basic.domain.elong.req.HotelReq;
import com.example.basic.domain.elong.resp.HotelInfoResp;
import com.example.basic.domain.elong.resp.HotelResp;
import com.example.basic.entity.ElongCity;
import com.example.basic.entity.ElongHotel;
import com.example.basic.entity.ElongHotelInfo;
import com.example.basic.utils.ElongHttp;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ElongHotelService {

  private static final Integer CORE_POOL_SIZE = 2;
  private static final Integer MAXIMUM_POOL_SIZE = 16;
  private static final Executor executor =
      new ThreadPoolExecutor(
          CORE_POOL_SIZE,
          MAXIMUM_POOL_SIZE,
          0L,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingDeque<>(3000));

  @Autowired private ElongCityService elongCityService;

  @Autowired private ElongHotelDao elongHotelDao;

  @Autowired private ElongHotelInfoDao elongHotelInfoDao;

  public void syncAllHotel() {
    List<ElongCity> cityList = elongCityService.selectCityList();
    int pageSize = 2000;
    int saveTotalCount = 0;
    for (ElongCity elongCity : cityList) {
      int pageIndex = 1;
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
            ElongHotel elongHotel = new ElongHotel();
            BeanUtils.copyProperties(hotel, elongHotel);
            elongHotels.add(elongHotel);
          }

          elongHotelDao.saveBatch(elongHotels);
          saveTotalCount += elongHotels.size();
          System.out.println("艺龙酒店同步-已保存hotel数量->" + saveTotalCount);
        } catch (Exception e) {
          System.err.println(
              "艺龙酒店同步-请求hotelList->elongCityId=" + elongCity.getCityId() + "，异常：" + e);
        } finally {
          pageIndex++;
        }
      }
    }
    System.out.println("艺龙酒店同步结束-已保存hotel数量->" + saveTotalCount);
  }

  public void syncAllHotelInfo() {
    Long startId = 0L;
    while (true) {
      long t1 = System.currentTimeMillis();
      List<ElongHotel> elongHotels = elongHotelDao.selectHotelIds(startId);
      if (CollectionUtils.isEmpty(elongHotels)) {
        break;
      }

      startId = elongHotels.stream().map(ElongHotel::getId).max(Long::compareTo).get();
      List<String> hotelIds = elongHotels.stream().map(ElongHotel::getHotelId).toList();
      List<ElongHotelInfo> elongHotelInfos = elongHotelInfoDao.selectHotelIds(hotelIds);
      Set<String> hotelIdSet =
          elongHotelInfos.stream().map(ElongHotelInfo::getHotelId).collect(Collectors.toSet());
      List<String> hotelInfoIds = Lists.newArrayList();
      for (String hotelId : hotelIds) {
        if (!hotelIdSet.contains(hotelId)) {
          hotelInfoIds.add(hotelId);
        }
      }

      if (CollectionUtils.isNotEmpty(hotelInfoIds)) {
        List<List<String>> subList = Lists.partition(hotelInfoIds, 20);
        CountDownLatch countDownLatch = new CountDownLatch(subList.size());
        for (List<String> subHotelInfoIds : subList) {
          executor.execute(() -> syncHotelInfos(subHotelInfoIds, countDownLatch));
        }
        try {
          countDownLatch.await();
        } catch (InterruptedException e) {
          System.err.println("艺龙酒店info同步-等待线程异常：" + e);
        }
      }

      long t2 = System.currentTimeMillis();
      System.out.println(
          "艺龙酒店info同步-已处理hotel数量->" + hotelInfoIds.size() + "，耗时：" + (t2 - t1) + "毫秒");
    }

    System.out.println("艺龙酒店info同步结束");
  }

  private void syncHotelInfos(List<String> hotelInfoIds, CountDownLatch countDownLatch) {
    try {
      String hotelInfoOptions = "1";
      List<ElongHotelInfo> hotelInfos = Lists.newArrayList();
      for (String hotelInfoId : hotelInfoIds) {
        HotelInfoReq req = new HotelInfoReq();
        req.setHotelId(hotelInfoId);
        req.setOptions(hotelInfoOptions);
        HotelInfoResp hotelInfoResp = this.getHotelInfo(req);
        if (hotelInfoResp == null
            || hotelInfoResp.getResult() == null
            || hotelInfoResp.getResult().getHotelDetail() == null) {
          if (hotelInfoResp != null && StringUtils.isNotEmpty(hotelInfoResp.getCode())) {
            System.err.println(
                "艺龙酒店info同步-请求hotelInfo->hotelInfoId="
                    + hotelInfoId
                    + "，code="
                    + hotelInfoResp.getCode());
          } else {
            System.err.println(
                "艺龙酒店info同步-请求hotelInfo->hotelInfoId=" + hotelInfoId + "，hotelInfoResp为空");
          }
          continue;
        }

        ElongHotelInfo hotelInfo = new ElongHotelInfo();
        BeanUtils.copyProperties(hotelInfoResp.getResult().getHotelDetail(), hotelInfo);
        hotelInfos.add(hotelInfo);
      }

      if (CollectionUtils.isNotEmpty(hotelInfos)) {
        elongHotelInfoDao.saveBatch(hotelInfos);
      }
    } catch (Exception e) {
      System.err.println("艺龙酒店info同步-请求hotelInfoList->异常：" + e);
    } finally {
      countDownLatch.countDown();
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
