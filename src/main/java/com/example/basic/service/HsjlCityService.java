package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.HsjlApiConfig;
import com.example.basic.domain.hsjl.req.HsjlCityReq;
import com.example.basic.domain.hsjl.resp.HsjlCityResp;
import com.example.basic.utils.HsjlHttp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class HsjlCityService {

  public HsjlCityResp getCityList(HsjlCityReq request) {
    String businessRequest = buildBusinessRequest(request);
    String responseBody =
        HsjlHttp.httpRequest(HsjlApiConfig.REQUEST_TYPE_HOTEL_CITY, businessRequest);
    if (StringUtils.isEmpty(responseBody)) {
      return null;
    }
    return JSON.parseObject(responseBody, HsjlCityResp.class);
  }

  private String buildBusinessRequest(HsjlCityReq request) {
    return "{\"countryCode\":\""
        + request.getCountryCode()
        + "\","
        + "\"haveLowestPrice\":\""
        + request.isHaveLowestPrice()
        + "\""
        + "}}";
  }
}
