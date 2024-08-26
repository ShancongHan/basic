package com.example.basic.utils;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

/**
 * @author han
 * @date 2024/8/26
 */
public class PoiUtils {

    public static double calculateMeter(Double latitude, Double longitude, Double latitude2, Double longitude2) {
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84,
                new GlobalCoordinates(latitude, longitude),
                new GlobalCoordinates(latitude2, longitude2));
        return geoCurve.getEllipsoidalDistance();
    }
}
