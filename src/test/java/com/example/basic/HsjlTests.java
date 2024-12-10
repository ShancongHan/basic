package com.example.basic;

import com.alibaba.fastjson2.JSON;
import com.example.basic.domain.hsjl.req.HsjlCityReq;
import com.example.basic.domain.hsjl.resp.HsjlCityResp;
import com.example.basic.service.HsjlCityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HsjlTests {

  @Autowired HsjlCityService hsjlCityService;

  @Test
  void testCity() {
    HsjlCityResp cityResp = hsjlCityService.getCityList(new HsjlCityReq());
    System.out.println(JSON.toJSONString(cityResp));
  }
}
