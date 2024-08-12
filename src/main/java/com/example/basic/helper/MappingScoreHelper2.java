package com.example.basic.helper;

import com.example.basic.entity.ExpediaPropertyBasic;
import com.example.basic.entity.JdJdbDaolv;
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
public class MappingScoreHelper2 {

    public static Integer calculateScore(ExpediaPropertyBasic expediaPropertyBasic, JdJdbDaolv jdJdbDaolv) {
        return calculateScore(expediaPropertyBasic.getNameEn(), expediaPropertyBasic.getLatitude(), expediaPropertyBasic.getLongitude(),
                expediaPropertyBasic.getAddress(), expediaPropertyBasic.getTelephone(), jdJdbDaolv.getName(),
                jdJdbDaolv.getLatitude(), jdJdbDaolv.getLongitude(), jdJdbDaolv.getAddress(), jdJdbDaolv.getTelephone());
    }

    public static Integer calculateScore(String name, BigDecimal latitude, BigDecimal longitude,
                                         String address, String phone, String daolvName, BigDecimal daolvLatitude,
                                         BigDecimal daolvLongitude, String daolvAddress, String telephone) {
        daolvName = handlerHotelName(daolvName);
        name = handlerHotelName(name);
        daolvAddress = handlerHotelAddress(daolvAddress);
        address = handlerHotelAddress(address);
        phone = handlerTel(phone);
        telephone = handlerTel(telephone);
        boolean dataIntact = Objects.nonNull(latitude) && Objects.nonNull(longitude)
                && Objects.nonNull(daolvLatitude) && Objects.nonNull(daolvLongitude);
        if (dataIntact) {
            double ellipsoidalDistance = calculateMeter(latitude, longitude, daolvLatitude, daolvLongitude);
            if (ellipsoidalDistance > 300d) {
                return calculateNegativeScore(daolvName, name, daolvAddress, address, phone, telephone);
            }
        }
        return calculateRealScore(daolvName, name, daolvAddress, address, phone, telephone);
    }

    private static Integer calculateRealScore(String daolvName, String name, String daolvAddress, String address, String phone, String telephone) {
        int score = 0;
        // 酒店相似 + 10分
        boolean sameNameOrSimilar = daolvName.equals(name) || name.startsWith(daolvName) || daolvName.startsWith(name);
        if (sameNameOrSimilar) {
            score += 10;
        }
        // 酒店地址相似 +5分
        boolean sameAddressOrSimilar = daolvAddress.equals(address) || address.startsWith(daolvAddress) || daolvAddress.startsWith(address);
        if (sameAddressOrSimilar) {
            score += 5;
        }
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
                                                  String daolvAddress, String address,
                                                  String phone, String telephone) {
        int score = 0;
        // 酒店相似 -10分
        boolean sameNameOrSimilar = daolvName.equals(name) || name.startsWith(daolvName) || daolvName.startsWith(name);
        if (sameNameOrSimilar) {
            score -= 10;
        }
        // 酒店地址相似 -5分
        boolean sameAddressOrSimilar = daolvAddress.equals(address) || address.startsWith(daolvAddress) || daolvAddress.startsWith(address);
        if (sameAddressOrSimilar) {
            score -= 5;
        }
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

    private static double calculateMeter(BigDecimal latitude, BigDecimal longitude, BigDecimal daolvLatitude, BigDecimal daolvLongitude) {
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84,
                new GlobalCoordinates(daolvLatitude.doubleValue(), daolvLongitude.doubleValue()),
                new GlobalCoordinates(latitude.doubleValue(), longitude.doubleValue()));
        return geoCurve.getEllipsoidalDistance();
    }

    public static Double calculateMeter(ExpediaPropertyBasic expediaPropertyBasic, JdJdbDaolv jdJdbDaolv) {
        boolean dataIntact = Objects.nonNull(expediaPropertyBasic.getLatitude()) && Objects.nonNull(expediaPropertyBasic.getLongitude())
                && Objects.nonNull(jdJdbDaolv.getLatitude()) && Objects.nonNull(jdJdbDaolv.getLongitude());
        if (!dataIntact) return null;
        return calculateMeter(expediaPropertyBasic.getLatitude(), expediaPropertyBasic.getLongitude(),
                jdJdbDaolv.getLatitude(), jdJdbDaolv.getLongitude());
    }

    public static String handlerTel(String telephone){
        return StringUtils.hasLength(telephone) ? telephone.replaceAll("-", "")
                .replaceAll("\\+", "")
                .replaceAll(" ", " ")
                .replaceAll(" +", "")
                .replaceAll("\\s+", "")
                .toUpperCase() : "***";
    }

    public static String handlerHotelAddress(String hotelAddress){
        return StringUtils.hasLength(hotelAddress) ? hotelAddress.replaceAll("-", "")
                .replaceAll("'", " ")
                .replaceAll(" ", " ")
                .replaceAll("\\.", " ")
                .replaceAll(",", " ")
                .replaceAll("/", " ")
                .replaceAll("&", "And")
                .replaceAll("\\s+", " ")
                .replaceAll(" +", "")
                .toUpperCase(): "***";
    }

    public static String handlerHotelName(String hotelName){
        return StringUtils.hasLength(hotelName) ? hotelName.replaceAll("-", "")
                .replaceAll("'", " ")
                .replaceAll(" ", " ")
                .replaceAll("\"", "")
                .replaceAll("&", "And")
                .replaceAll("\\s+", " ")
                .replaceAll(" +", "")
                .toUpperCase(): "***";
    }
}
