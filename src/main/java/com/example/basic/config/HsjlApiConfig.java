package com.example.basic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HsjlApiConfig {

  public static final String VERSION = "1.0.0";

  public static final String REQUEST_TYPE_HOTEL_CITY = "queryCityList";

  public static final String REQUEST_TYPE_HOTEL_LIST = "queryHotelIdList";

  public static final String REQUEST_TYPE_HOTEL_INFO = "queryHotelInfo";

  public static String apiUrl;

  public static String secureKey;

  public static String partnerCode;

  @Value("${api.param.hsjl.api-url}")
  public void setApiUrl(String apiUrl) {
    HsjlApiConfig.apiUrl = apiUrl;
  }

  @Value("${api.param.hsjl.secure-key}")
  public void setSecureKey(String secureKey) {
    HsjlApiConfig.secureKey = secureKey;
  }

  @Value("${api.param.hsjl.partner-code}")
  public void setPartnerCode(String partnerCode) {
    HsjlApiConfig.partnerCode = partnerCode;
  }
}
