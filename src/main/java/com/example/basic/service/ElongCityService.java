package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.config.ElongApiConfig;
import com.example.basic.dao.ElongCityDao;
import com.example.basic.domain.elong.req.CityReq;
import com.example.basic.domain.elong.resp.CityResp;
import com.example.basic.entity.ElongCity;
import com.example.basic.utils.ElongHttp;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElongCityService {

  @Resource private ElongCityDao elongCityDao;

  public List<ElongCity> selectCityList() {
    return elongCityDao.selectCityList();
  }

  public void syncAllCities() {
    int countryType = 1;
    int cityIdType = 1;
    int pageIndex = 1;
    int pageSize = 200;
    while (true) {
      try {
        CityReq req = new CityReq();
        req.setCountryType(countryType);
        req.setCityIdType(cityIdType);
        req.setPageIndex(pageIndex);
        req.setPageSize(pageSize);
        CityResp cityResp = this.getCityList(req);
        if (cityResp == null
            || cityResp.getResult() == null
            || CollectionUtils.isEmpty(cityResp.getResult().getCitys())) {
          break;
        }

        List<ElongCity> elongCities = Lists.newArrayList();
        cityResp
            .getResult()
            .getCitys()
            .forEach(
                city -> {
                  ElongCity elongCity = new ElongCity();
                  BeanUtils.copyProperties(city, elongCity);
                  elongCities.add(elongCity);
                });

        elongCityDao.saveBatch(elongCities);
        System.out.println("已完成cityList：" + pageIndex * pageSize);
      } catch (Exception e) {
        System.err.println("请求cityList异常：" + e);
      } finally {
        pageIndex++;
      }
    }

    System.out.println("请求cityList结束");
  }

  public CityResp getCityList(CityReq request) {
    String data = buildData(request);
    String responseBody = ElongHttp.httpRequest(ElongApiConfig.URL_HOTEL_CITY, data);
    if (StringUtils.isEmpty(responseBody)) {
      return null;
    }
    return JSON.parseObject(responseBody, CityResp.class);
  }

  private String buildData(CityReq request) {
    return "{\"Version\":\""
        + ElongApiConfig.VERSION
        + "\","
        + "\"Local\":\""
        + ElongApiConfig.LOCAL
        + "\","
        + "\"Request\":{"
        + "\"CountryType\":\""
        + request.getCountryType()
        + "\","
        + "\"CityIdType\":\""
        + request.getCityIdType()
        + "\","
        + "\"IsNeedLocation\":\""
        + request.isNeedLocation()
        + "\","
        + "\"PageSize\":\""
        + request.getPageSize()
        + "\","
        + "\"PageIndex\":\""
        + request.getPageIndex()
        + "\""
        + "}}";
  }
}
