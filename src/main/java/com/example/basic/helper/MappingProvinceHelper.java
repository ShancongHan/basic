package com.example.basic.helper;

import org.springframework.util.StringUtils;

import java.text.Normalizer;

/**
 * @author han
 * @date 2024/12/12
 */
public class MappingProvinceHelper {

    public static boolean match(String name, String provinceName, boolean chinese) {
        name = handler(name);
        provinceName = handler(provinceName);
        if (!chinese) {
            provinceName = removeAccents(provinceName);
        }
        return name.equals(provinceName);
    }

    private static String handler(String telephone){
        return StringUtils.hasLength(telephone) ? telephone.replaceAll("State", "")
                .replaceAll(" ", " ")
                .replaceAll("-", " ")
                .replaceAll("Department", " ")
                .replaceAll("Emirate of ", " ")
                .replaceAll("Province", " ")
                .replaceAll("District", " ")
                .replaceAll("County", " ")
                .replaceAll("county", " ")
                .replaceAll("St.", " ")
                .replaceAll("Saint ", " ")
                .replaceAll("Parish", " ")
                .replaceAll("Division", " ")
                .replaceAll("Krai", " ")
                .replaceAll("Oblast", " ")
                .replaceAll("region", " ")
                .replaceAll("Region", " ")
                .replaceAll("Governorate", " ")
                .replaceAll("Governate", " ")
                .replaceAll("Republic of ", " ")
                .replaceAll("Island", " ")
                .replaceAll("Islands", " ")
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
