package com.example.basic.helper;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author han
 * @date 2024/11/11
 */
public class GnMappingScoreHelper {

    public static Integer calculateScore(String name, BigDecimal latitude, BigDecimal longitude,
                                         String phone, String name2, BigDecimal latitude2,
                                         BigDecimal longitude2, String phone2) {
        name = handlerHotelName(name);
        name2 = handlerHotelName(name2);
        phone = handlerTel(phone);
        phone2 = handlerTel(phone2);
        boolean dataIntact = Objects.nonNull(latitude) && Objects.nonNull(longitude)
                && Objects.nonNull(latitude2) && Objects.nonNull(longitude2);
        if (dataIntact) {
            double ellipsoidalDistance = calculateMeter(latitude, longitude, latitude2, longitude2);
            if (ellipsoidalDistance > 300d) {
                return calculateNegativeScore(name2, name, phone, phone2);
            }
        }
        return calculateRealScore(name2, name, phone, phone2);
    }

    private static Integer calculateRealScore(String daolvName, String name, String phone, String telephone) {
        int score = 0;
        // 酒店相似 + 10分
        score += compareHotelName(name, daolvName);
        // 酒店地址相似 +5分
       /* boolean sameAddressOrSimilar = daolvAddress.equals(address) || address.startsWith(daolvAddress) || daolvAddress.startsWith(address);
        if (sameAddressOrSimilar) {
            score += 5;
        }*/
        // 电话相似 +1分
        boolean sameTelOrSimilar = phone.length() > 3 && telephone.length() > 3
                && (telephone.equals(phone) || phone.startsWith(telephone)
                || telephone.startsWith(phone)
                || telephone.contains(phone)
                || phone.contains(telephone));
        if (sameTelOrSimilar) {
            score += 1;
        }
        return score;
    }

    private static Integer calculateNegativeScore(String daolvName, String name,
                                                  String phone, String telephone) {
        int score = 0;
        // 酒店相似 -10分
        score -= compareHotelName(name, daolvName);
        // 酒店地址相似 -5分
        /*boolean sameAddressOrSimilar = daolvAddress.equals(address) || address.startsWith(daolvAddress) || daolvAddress.startsWith(address);
        if (sameAddressOrSimilar) {
            score -= 5;
        }*/
        // 电话相似 -1分
        boolean sameTelOrSimilar = phone.length() > 3 && telephone.length() > 3
                && (telephone.equals(phone) || phone.startsWith(telephone)
                || telephone.startsWith(phone)
                || telephone.contains(phone)
                || phone.contains(telephone));
        if (sameTelOrSimilar) {
            score -= 1;
        }
        return score;
    }

    public static double calculateMeter(BigDecimal latitude, BigDecimal longitude, BigDecimal daolvLatitude, BigDecimal daolvLongitude) {
        if (latitude ==null || longitude == null || daolvLatitude == null || daolvLongitude == null) return -1;
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84,
                new GlobalCoordinates(daolvLatitude.doubleValue(), daolvLongitude.doubleValue()),
                new GlobalCoordinates(latitude.doubleValue(), longitude.doubleValue()));
        return geoCurve.getEllipsoidalDistance();
    }

    public static String handlerHotelName(String hotelName) {
        return StringUtils.hasLength(hotelName) ? hotelName.trim().toUpperCase() : "***";
    }

    public static String handlerTel(String telephone) {
        return  StringUtils.hasLength(telephone) ? telephone.replaceAll("-", "")
                .replaceAll("（","")
                .replaceAll("）","")
                .replaceAll("—","")
                .replaceAll("\\+", "")
                .replaceAll("、", "")
                .replaceAll("/", "")
                .replaceAll("前台预订热线：", "")
                .replaceAll("值班经理热线：", "")
                .replaceAll(" +", "")
                .trim()
                .toUpperCase() : "***";
    }

    private static Integer compareHotelName(String name, String name2) {
        JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();
        Double apply = jaroWinklerDistance.apply(name, name2);
        Double match = 0.93d;
        return apply.compareTo(match) > 0 ? 10 : 0;
    }
}
