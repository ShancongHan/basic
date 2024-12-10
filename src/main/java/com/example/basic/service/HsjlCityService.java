package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.HsjlApiConfig;
import com.example.basic.domain.hsjl.req.HsjlCityReq;
import com.example.basic.domain.hsjl.resp.HsjlCityResp;
import com.example.basic.utils.HsjlHttp;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HsjlCityService {

  public HsjlCityResp getCityList(HsjlCityReq request) {
    Map<String, Object> businessRequest = this.buildBusinessRequest(request);
    String responseBody =
        HsjlHttp.httpRequest(HsjlApiConfig.REQUEST_TYPE_HOTEL_CITY, businessRequest);
    if (StringUtils.isEmpty(responseBody)) {
      return null;
    }
    return JSON.parseObject(responseBody, HsjlCityResp.class);
  }

  private Map<String, Object> buildBusinessRequest(HsjlCityReq request) {
    Map<String, Object> businessRequest = Maps.newHashMap();
    businessRequest.put("countryCode", request.getCountryCode());
    businessRequest.put("haveLowestPrice", request.isHaveLowestPrice());
    return businessRequest;
  }
}
