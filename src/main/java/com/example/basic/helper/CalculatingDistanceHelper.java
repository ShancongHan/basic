package com.example.basic.helper;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/7/10
 */
public class CalculatingDistanceHelper {

    /**
     * 距离，单位(米)
     * @param sourceLatitude 原点纬度
     * @param sourceLongitude 原点经度
     * @param targetLatitude    目标点维度
     * @param targetLongitude   目标点经度
     * @return meters
     */
    public static double calculateMeters(String sourceLatitude, String sourceLongitude, BigDecimal targetLatitude, BigDecimal targetLongitude) {
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84,
                new GlobalCoordinates(targetLatitude.doubleValue(), targetLongitude.doubleValue()),
                new GlobalCoordinates(Double.parseDouble(sourceLatitude), Double.parseDouble(sourceLongitude)));
        return geoCurve.getEllipsoidalDistance();
    }
}
