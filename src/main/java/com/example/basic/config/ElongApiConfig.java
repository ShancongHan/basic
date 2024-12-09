package com.example.basic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ElongApiConfig {

  public static final String VERSION = "1.65";

  public static final String LOCAL = "zh_CN";

  public static final String URL_HOTEL_CITY = "hotel.static.city";

  public static final String URL_HOTEL_LIST = "hotel.static.list";

  public static final String URL_HOTEL_INFO = "hotel.static.info";

  public static String apiUrl;

  public static String userName;

  public static String appKey;

  public static String secretKey;

  @Value("${api.param.elong.api-url}")
  public void setApiUrl(String apiUrl) {
    ElongApiConfig.apiUrl = apiUrl;
  }

  @Value("${api.param.elong.user-name}")
  public void setUserName(String userName) {
    ElongApiConfig.userName = userName;
  }

  @Value("${api.param.elong.app-key}")
  public void setAppKey(String appKey) {
    ElongApiConfig.appKey = appKey;
  }

  @Value("${api.param.elong.secret-key}")
  public void setSecretKey(String secretKey) {
    ElongApiConfig.secretKey = secretKey;
  }
}
