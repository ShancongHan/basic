package com.example.basic.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignatureUtils {

  public static String calculateSignature(
      String timestamp, String data, String appKey, String secretKey) {
    try {
      // 计算data的MD5值
      String md5Data = md5(data + appKey);
      // 拼接字符串
      String toSign = timestamp + md5Data + secretKey;
      // 计算签名
      return md5(toSign).toLowerCase();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 红色加力签名 signature = UPPER(MD5(timestamp + partnerCode + UPPER(MD5(secureKey)) + requestType))
   */
  public static String hsjlSignature(
      String timestamp, String secureKey, String partnerCode, String requestType) {
    try {

      // 计算secureKey的MD5值
      String md5SecureKey = md5(secureKey).toUpperCase();

      // 拼接字符串
      String toSign = timestamp + partnerCode + md5SecureKey + requestType;

      // 计算签名
      return md5(toSign).toUpperCase();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static String md5(String input) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
    StringBuilder hexString = new StringBuilder();
    for (byte b : digest) {
      hexString.append(String.format("%02x", b));
    }
    return hexString.toString();
  }
}
