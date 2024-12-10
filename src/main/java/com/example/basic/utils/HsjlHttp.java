package com.example.basic.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import com.example.basic.config.HsjlApiConfig;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class HsjlHttp {

  public static String httpRequest(String requestType, String businessRequest) {
    RestTemplate restTemplate = new RestTemplate();

    // 设置请求头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add("Accept-Encoding", "gzip");
    headers.add("Connection", "keep-alive");
    String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
    headers.add("timestamp", timestamp);
    headers.add("partnerCode", HsjlApiConfig.partnerCode);
    headers.add("requestType", requestType);
    headers.add("version", HsjlApiConfig.VERSION);
    headers.add("gzip", "1");
    headers.add(
        "signature",
        SignatureUtils.hsjlSignature(
            timestamp, HsjlApiConfig.secureKey, HsjlApiConfig.partnerCode, requestType));

    // 构建请求参数
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("businessRequest", businessRequest);

    // 构建请求URL
    URI uri =
        UriComponentsBuilder.fromUriString(HsjlApiConfig.apiUrl)
            .queryParams(params)
            .build()
            .encode(StandardCharsets.UTF_8)
            .toUri();

    // 发送请求并获取响应
    ResponseEntity<byte[]> response =
        restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(headers), byte[].class);

    // 检查响应内容类型
    if (response.getStatusCode().is2xxSuccessful()) {
      byte[] responseBodyBytes = response.getBody();
      if (responseBodyBytes != null) {
        try (InputStream is = new GZIPInputStream(new ByteArrayInputStream(responseBodyBytes))) {
          return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
          throw new RuntimeException("Failed to decompress response: " + e.getMessage(), e);
        }
      }
    } else {
      throw new RuntimeException("Failed to get response: " + response.getStatusCode());
    }
    return null;
  }
}
