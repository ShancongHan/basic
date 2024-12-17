package com.example.basic.web;

import com.example.basic.service.GlobalHotelService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 国际酒店打底
 *
 * @author han
 * @date 2024/12/11
 */
@RestController
@RequestMapping(value = "global/hotel")
public class GlobalHotelController {

    @Resource
    private GlobalHotelService globalHotelService;

    /**
     * 分析携程和eps国家匹配情况
     */
    @PostMapping(value = "analyze-country")
    public void analyzeCountry() {
        try {
            globalHotelService.analyzeCountry();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 分析省份
     */
    @PostMapping(value = "analyze-province")
    public void analyzeProvince() {
        try {
            globalHotelService.analyzeProvince();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 匹配省份
     */
    @PostMapping(value = "match-province")
    public void matchProvince() {
        try {
            globalHotelService.matchProvince();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 分析城市
     */
    @PostMapping(value = "analyze-city")
    public void analyzeCity() {
        try {
            globalHotelService.analyzeCity();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
