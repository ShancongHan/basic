package com.example.basic.helper;

import com.example.basic.entity.JdJdbDaolv;
import com.example.basic.entity.WebbedsHotelData;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author han
 * date 2024/5/31
 */
public class MappingScoreHelper {

    public static Integer calculateScore(WebbedsHotelData webbedsHotelData, JdJdbDaolv jdJdbDaolv) {
        return calculateScore(webbedsHotelData.getHotelName(), webbedsHotelData.getLatitude(), webbedsHotelData.getLongitude(),
                webbedsHotelData.getHotelAddress(), webbedsHotelData.getReservationTelephone(), jdJdbDaolv.getName(),
                jdJdbDaolv.getLatitude(), jdJdbDaolv.getLongitude(), jdJdbDaolv.getAddress(), jdJdbDaolv.getTelephone());
    }
    public static Double calculateMeter(WebbedsHotelData webbedsHotelData, JdJdbDaolv jdJdbDaolv) {
        boolean dataIntact = StringUtils.hasLength(webbedsHotelData.getLatitude()) && StringUtils.hasLength(webbedsHotelData.getLongitude())
                && Objects.nonNull(jdJdbDaolv.getLatitude()) && Objects.nonNull(jdJdbDaolv.getLongitude());
        if (!dataIntact) return null;
        return calculateMeter(webbedsHotelData.getLatitude(), webbedsHotelData.getLongitude(),
                jdJdbDaolv.getLatitude(), jdJdbDaolv.getLongitude());
    }
    public static Integer calculateScore(String webbedsName, String webbedsLatitude, String webbedsLongitude,
                                         String webbedsAddress, String webbedsTelephone,String daolvName, BigDecimal daolvLatitude,
                                         BigDecimal daolvLongitude, String daolvAddress, String telephone) {
        daolvName = handlerHotelName(daolvName);
        webbedsName = handlerHotelName(webbedsName);
        daolvAddress = handlerHotelAddress(daolvAddress);
        webbedsAddress = handlerHotelAddress(webbedsAddress);
        telephone = handlerTel(telephone);
        webbedsTelephone = handlerTel(webbedsTelephone);

        boolean dataIntact = StringUtils.hasLength(webbedsLatitude) && StringUtils.hasLength(webbedsLongitude)
                && Objects.nonNull(daolvLatitude) && Objects.nonNull(daolvLongitude);
        if (dataIntact) {
            double ellipsoidalDistance = calculateMeter(webbedsLatitude, webbedsLongitude, daolvLatitude, daolvLongitude);
            if (ellipsoidalDistance > 300d) {
                return calculateNegativeScore(daolvName, webbedsName, daolvAddress, webbedsAddress, webbedsTelephone, telephone);
            }
        }
        return calculateRealScore(daolvName, webbedsName, daolvAddress, webbedsAddress, webbedsTelephone, telephone);
    }

    private static Integer calculateRealScore(String daolvName, String webbedsName, String daolvAddress, String webbedsAddress, String webbedsTelephone, String telephone) {
        int score = 0;
        // 酒店相似 + 10分
        boolean sameNameOrSimilar = daolvName.equals(webbedsName) || webbedsName.startsWith(daolvName) || daolvName.startsWith(webbedsName);
        if (sameNameOrSimilar) {
            score += 10;
        }
        // 酒店地址相似 +5分
        boolean sameAddressOrSimilar = daolvAddress.equals(webbedsAddress) || webbedsAddress.startsWith(daolvAddress) || daolvAddress.startsWith(webbedsAddress);
        if (sameAddressOrSimilar) {
            score += 5;
        }
        // 电话相似 +1分
        boolean sameTelOrSimilar = webbedsTelephone.length() > 3 && telephone.length() > 3
                && (telephone.equals(webbedsTelephone) || webbedsTelephone.startsWith(telephone)
                || telephone.startsWith(webbedsTelephone)
                || telephone.contains(webbedsTelephone)
                || webbedsTelephone.contains(telephone));
        if (sameTelOrSimilar) {
            score += 1;
        }
        return score;
    }

    private static Integer calculateNegativeScore(String daolvName, String webbedsName,
                                                  String daolvAddress, String webbedsAddress,
                                                  String webbedsTelephone, String telephone) {
        int score = 0;
        // 酒店相似 -10分
        boolean sameNameOrSimilar = daolvName.equals(webbedsName) || webbedsName.startsWith(daolvName) || daolvName.startsWith(webbedsName);
        if (sameNameOrSimilar) {
            score -= 10;
        }
        // 酒店地址相似 -5分
        boolean sameAddressOrSimilar = daolvAddress.equals(webbedsAddress) || webbedsAddress.startsWith(daolvAddress) || daolvAddress.startsWith(webbedsAddress);
        if (sameAddressOrSimilar) {
            score -= 5;
        }
        // 电话相似 -1分
        boolean sameTelOrSimilar = webbedsTelephone.length() > 3 && telephone.length() > 3
                && (telephone.equals(webbedsTelephone) || webbedsTelephone.startsWith(telephone)
                || telephone.startsWith(webbedsTelephone)
                || telephone.contains(webbedsTelephone)
                || webbedsTelephone.contains(telephone));
        if (sameTelOrSimilar) {
            score -= 1;
        }
        return score;
    }

    private static double calculateMeter(String webbedsLatitude, String webbedsLongitude, BigDecimal daolvLatitude, BigDecimal daolvLongitude) {
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84,
                new GlobalCoordinates(daolvLatitude.doubleValue(), daolvLongitude.doubleValue()),
                new GlobalCoordinates(Double.parseDouble(webbedsLatitude), Double.parseDouble(webbedsLongitude)));
        return geoCurve.getEllipsoidalDistance();
    }

    public static String handlerTel(String telephone){
        return StringUtils.hasLength(telephone) ? telephone.replaceAll("-", "")
                .replaceAll("\\+", "")
                .replaceAll("\\s+", "")
                .toUpperCase() : "***";
    }

    public static String handlerHotelName(String hotelName){
        return StringUtils.hasLength(hotelName) ? hotelName.replaceAll("-", "")
                .replaceAll("'", " ")
                .replaceAll("&", "And")
                .replaceAll("\\s+", " ")
                .toUpperCase(): "***";
    }

    public static String handlerHotelAddress(String hotelAddress){
        return StringUtils.hasLength(hotelAddress) ? hotelAddress.replaceAll("-", "")
                .replaceAll("'", " ")
                .replaceAll("\\.", " ")
                .replaceAll(",", " ")
                .replaceAll("/", " ")
                .replaceAll("&", "And")
                .replaceAll("\\s+", " ")
                .toUpperCase(): "***";
    }
}
