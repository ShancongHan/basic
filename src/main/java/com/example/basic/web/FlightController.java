package com.example.basic.web;

import com.example.basic.service.FlightService;
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
@RequestMapping(value = "flight")
public class FlightController {

    @Resource
    private FlightService flightService;

    @PostMapping(value = "airport/fresh")
    public void airportFresh() {
        try {
            flightService.airportFresh();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "airport/update1")
    public void airportUpdate1() {
        try {
            flightService.airportUpdate1();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }


    @PostMapping(value = "airport/test")
    public void airportTest() {
        try {
            flightService.airportTest();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "airport")
    public void airport() {
        try {
            flightService.airport();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "airport/find")
    public void airportFind() {
        try {
            flightService.airportFind();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "airport/update2")
    public void airportUpdate2() {
        try {
            flightService.airportUpdate2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "airport/update3")
    public void airportUpdate3() {
        try {
            flightService.airportUpdate3();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
