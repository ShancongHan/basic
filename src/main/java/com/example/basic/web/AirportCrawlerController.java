package com.example.basic.web;

import com.example.basic.service.AirportCrawlerService;
import com.example.basic.service.FlightService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author han
 * @date 2024/7/1
 */
@RestController
@RequestMapping(value = "crawler")
public class AirportCrawlerController {

    @Resource
    private AirportCrawlerService airportCrawlerService;

    @PostMapping(value = "airport")
    public void airport() {
        try {
            airportCrawlerService.airport();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "airport-cn")
    public void airportCn() {
        try {
            airportCrawlerService.airportCn();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
