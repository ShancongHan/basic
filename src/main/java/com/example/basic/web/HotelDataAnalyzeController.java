package com.example.basic.web;

import com.example.basic.service.BasicDataService;
import com.example.basic.service.HotelDataAnalyzeService;
import com.example.basic.service.JdGlobalGeoService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author han
 * @date 2024/4/30
 */
@RestController
@RequestMapping(value = "hotel")
public class HotelDataAnalyzeController {

    @Resource
    private HotelDataAnalyzeService hotelDataAnalyzeService;

    @Resource
    private JdGlobalGeoService jdGlobalGeoService;

    @Resource
    private BasicDataService basicDataService;

    @PostMapping(value = "analyze")
    public void analyze() {
        try {
            hotelDataAnalyzeService.analyze();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "basic/data")
    public void basicData() {
        try {
            jdGlobalGeoService.basicData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "compare/data")
    public void compareData() {
        try {
            basicDataService.compare();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "handler/country")
    public void handlerCountry() {
        try {
            basicDataService.handlerCountry();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "handler/province")
    public void handlerProvince() {
        try {
            basicDataService.handlerProvince();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "handler/single/province")
    public void handlerSingleProvince(String countryCode) {
        try {
            basicDataService.handlerSingleProvince(countryCode);
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "compare/airport/data")
    public void compareAirportData() {
        try {
            basicDataService.compareAirportData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "delete/city")
    public void deleteCity() {
        try {
            basicDataService.deleteCity();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "handler/city")
    public void handlerCity() {
        try {
            basicDataService.handlerCity();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
