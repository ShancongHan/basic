package com.example.basic.helper;

import org.springframework.util.StringUtils;

import java.text.Normalizer;

/**
 * @author han
 * @date 2025/1/10
 */
public class MappingCityHelper {

    /**
     * 判断是否相同
     *
     * @param name 名字
     * @param otherName 其他名字
     * @param nameEn 英文名
     * @param otherNameEn 其他英文名
     * @return true-相同;false-不同
     */
    public static boolean match(String name, String otherName, String nameEn, String otherNameEn) {
        if ("康达".equals(name) && "Jaguas".equals(otherName)) {
            System.out.println("xxxxx");
        }
        return matchName(name, otherName) || matchNameEn(nameEn, otherNameEn);
    }

    public static boolean matchName(String name, String otherName) {
        name = handler(name);
        otherName = handler(otherName);
        return name.equals(otherName);
    }

    public static boolean matchNameEn(String name, String otherName) {
        name = handlerEn(name);
        otherName = handlerEn(otherName);
        otherName = removeAccents(otherName);
        return name.equals(otherName);
    }

    private static String handler(String name){
        return StringUtils.hasLength(name) ? name.replaceAll("市", "")
                .replaceAll(" ", " ")
                .replaceAll(" +", "")
                .toUpperCase() : "***";
    }

    private static String handlerEn(String name){
        return StringUtils.hasLength(name) ? name.replaceAll("St.", "")
                .replaceAll(" ", " ")
                .replaceAll("\\(", " ")
                .replaceAll("（", " ")
                .replaceAll("\\)", " ")
                .replaceAll("）", " ")
                .replaceAll("&", "And")
                .replaceAll("-", " ")
                .replaceAll("'", " ")
                .replaceAll("Al", " ")
                .replaceAll("\\.", " ")
                .replaceAll(" +", "")
                .toUpperCase() : "***";
    }

    public static String removeAccents(String input) {
        // 1. 先将字符规范化（NFD：分解为字符加重音符号）
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // 2. 使用正则表达式去除所有非字母字符（即重音符号）
        return normalized.replaceAll("[^\\p{ASCII}]", "");
    }
}
