package com.example.basic;

import com.alibaba.fastjson2.JSON;
import com.example.basic.domain.hsjl.req.HsjlCityReq;
import com.example.basic.domain.hsjl.req.HsjlHotelInfoReq;
import com.example.basic.domain.hsjl.req.HsjlHotelReq;
import com.example.basic.domain.hsjl.resp.HsjlCityResp;
import com.example.basic.domain.hsjl.resp.HsjlHotelInfoResp;
import com.example.basic.domain.hsjl.resp.HsjlHotelResp;
import com.example.basic.service.HsjlCityService;
import com.example.basic.service.HsjlHotelService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HsjlTests {

  @Autowired HsjlCityService hsjlCityService;

  @Autowired HsjlHotelService hsjlHotelService;

  @Test
  void testCity() {
    HsjlCityResp cityResp = hsjlCityService.getCityList(new HsjlCityReq());
    System.out.println(JSON.toJSONString(cityResp));
  }

  @Test
  void testSyncCity() {
    hsjlCityService.syncCityList();
    System.out.println("sync OK");
  }

  @Test
  void testHotel() {
    HsjlHotelReq hsjlHotelReq = new HsjlHotelReq();
    hsjlHotelReq.setCityCode("xixian");
    hsjlHotelReq.setPageNo(1);
    HsjlHotelResp hotelResp = hsjlHotelService.getHotelIds(hsjlHotelReq);
    System.out.println(JSON.toJSONString(hotelResp));
  }

  @Test
  void testSyncAllHotelList() {
    hsjlHotelService.syncAllHotelList();
    System.out.println("sync OK");
  }

  @Test
  void testHotelInfo() {
    HsjlHotelInfoReq req = new HsjlHotelInfoReq();
    req.setHotelIds(Lists.newArrayList(122432L, 122501L, 122539L));
    HsjlHotelInfoResp hotelInfos = hsjlHotelService.getHotelInfos(req);
    System.out.println(JSON.toJSONString(hotelInfos));
  }

  @Test
  void testSyncAllHotelInfos() {
    hsjlHotelService.syncAllHotelInfos();
    System.out.println("sync OK");
  }
}
