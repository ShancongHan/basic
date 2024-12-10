package com.example.basic;

import com.alibaba.fastjson2.JSON;
import com.example.basic.domain.elong.req.CityReq;
import com.example.basic.domain.elong.req.HotelInfoReq;
import com.example.basic.domain.elong.req.HotelReq;
import com.example.basic.domain.elong.resp.CityResp;
import com.example.basic.domain.elong.resp.HotelInfoResp;
import com.example.basic.domain.elong.resp.HotelResp;
import com.example.basic.service.ElongCityService;
import com.example.basic.service.ElongHotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ElongTests {

  @Autowired ElongCityService elongCityService;

  @Autowired ElongHotelService elongHotelService;

  @Test
  void testCity() {
    CityReq req = new CityReq();
    req.setCountryType(1);
    req.setCityIdType(1);
    req.setPageIndex(1);
    req.setPageSize(200);
    CityResp cityResp = elongCityService.getCityList(req);
    System.out.println(JSON.toJSONString(cityResp));
  }

  @Test
  void testSyncAllCities() {
    elongCityService.syncAllCities();
  }

  @Test
  void testHotelList() {
    HotelReq req = new HotelReq();
    req.setCityId("0101");
    req.setPageIndex(1);
    req.setPageSize(2000);
    HotelResp hotelResp = elongHotelService.getHotelList(req);
    System.out.println(JSON.toJSONString(hotelResp));
  }

  @Test
  void testSyncAllHotels() {
    elongHotelService.syncAllHotel();
  }

  @Test
  void testHotelInfo() {
    HotelInfoReq req = new HotelInfoReq();
    req.setHotelId("94405767");
    req.setOptions("1");
    HotelInfoResp hotelInfo = elongHotelService.getHotelInfo(req);
    System.out.println(JSON.toJSONString(hotelInfo));
  }
}
