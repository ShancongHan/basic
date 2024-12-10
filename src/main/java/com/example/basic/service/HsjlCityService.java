package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.HsjlApiConfig;
import com.example.basic.dao.HsjlCityDao;
import com.example.basic.domain.hsjl.req.HsjlCityReq;
import com.example.basic.domain.hsjl.resp.HsjlCityResp;
import com.example.basic.entity.HsjlCity;
import com.example.basic.utils.HsjlHttp;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class HsjlCityService {

  @Autowired private HsjlCityDao hsjlCityDao;

  public List<HsjlCity> selectCityList() {
    return hsjlCityDao.selectCityList();
  }

  public void syncCityList() {
    HsjlCityResp cityResp = this.getCityList(new HsjlCityReq());
    if (Objects.nonNull(cityResp)
        && Objects.nonNull(cityResp.getBussinessResponse())
        && CollectionUtils.isNotEmpty(cityResp.getBussinessResponse().getProvinces())) {
      List<HsjlCity> insertCities = Lists.newArrayList();
      for (HsjlCityResp.Province province : cityResp.getBussinessResponse().getProvinces()) {
        for (HsjlCityResp.City city : province.getCitys()) {
          HsjlCity hsjlCity = new HsjlCity();
          hsjlCity.setCityCode(city.getCityCode());
          hsjlCity.setCityName(city.getCityName());
          hsjlCity.setProvinceCode(province.getProvinceCode());
          hsjlCity.setProvinceName(province.getProvinceName());
          hsjlCity.setCityNameEn(city.getAreaEngName());
          hsjlCity.setProvinceNameEn(province.getProvinceEngName());
          insertCities.add(hsjlCity);
        }
      }

      if (CollectionUtils.isNotEmpty(insertCities)) {

        // 将insertCities分成200个为一组的List<List<HsjlCity>> subList
        List<List<HsjlCity>> subList = Lists.partition(insertCities, 200);
        for (List<HsjlCity> hsjlCities : subList) {
          hsjlCityDao.saveBatch(hsjlCities);
        }
      }
    }
  }

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
