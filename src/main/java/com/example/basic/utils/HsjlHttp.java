package com.example.basic.utils;

import java.util.Map;
import com.example.basic.config.HsjlApiConfig;
import com.google.common.collect.Maps;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class HsjlHttp {

  public static String httpRequest(String requestType, Map<String, Object> businessRequest) {

    // 设置请求头
    String timestamp = String.valueOf(System.currentTimeMillis());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Connection", "keep-alive");
    headers.set("partnerCode", HsjlApiConfig.partnerCode);
    headers.set("requestType", requestType);
    headers.set(
        "signature",
        SignatureUtils.hsjlSignature(
            timestamp, HsjlApiConfig.secureKey, HsjlApiConfig.partnerCode, requestType));
    headers.set("timestamp", timestamp);
    headers.set("version", HsjlApiConfig.VERSION);
    headers.set("gzip", "0");

    // 构建请求参数
    Map<String, Object> requestBody = Maps.newHashMap();
    requestBody.put("businessRequest", businessRequest);
    requestBody.put("header", headers.toSingleValueMap());

    // 创建RestTemplate实例
    RestTemplate restTemplate = new RestTemplate();

    // 发送POST请求
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
    ResponseEntity<String> responseEntity =
        restTemplate.exchange(
            HsjlApiConfig.apiUrl + requestType, HttpMethod.POST, requestEntity, String.class);

    return responseEntity.getBody();
  }
}
