package com.example.basic.utils;

import com.example.basic.config.ElongApiConfig;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class ElongHttp {

  public static String httpRequest(String method, String data) {
    RestTemplate restTemplate = new RestTemplate();

    // 设置请求头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add("Accept-Encoding", "gzip");
    headers.add("Connection", "keep-alive");

    // 构建公共请求参数
    String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
    String signature =
        SignatureUtils.calculateSignature(
            timestamp, data, ElongApiConfig.appKey, ElongApiConfig.secretKey);

    // 构建请求参数
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("user", ElongApiConfig.userName);
    params.add("method", method);
    params.add("timestamp", timestamp);
    params.add("format", "json");
    params.add("data", data);
    params.add("signature", signature);

    // 构建请求URL
    URI uri =
        UriComponentsBuilder.fromUriString(ElongApiConfig.apiUrl)
            .queryParams(params)
            .build()
            .encode(StandardCharsets.UTF_8)
            .toUri();

    // 发送请求并获取响应
    ResponseEntity<byte[]> response =
        restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);

    // 检查响应内容类型
    if (response.getStatusCode().is2xxSuccessful()) {
      byte[] responseBodyBytes = response.getBody();
      if (responseBodyBytes != null) {
        try (InputStream is = new GZIPInputStream(new ByteArrayInputStream(responseBodyBytes))) {
          String responseBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
          if (responseBody.contains("html")) {
            throw new RuntimeException("Unexpected HTML response from server: " + responseBody);
          }

          return responseBody;
        } catch (IOException e) {
          throw new RuntimeException("Failed to decompress response: " + e.getMessage(), e);
        }
      }
    } else {
      throw new RuntimeException("Failed to get city list: " + response.getStatusCode());
    }
    return null;
  }
}
