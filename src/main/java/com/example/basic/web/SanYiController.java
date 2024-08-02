package com.example.basic.web;

import com.example.basic.service.SanYiService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author han
 * @date 2024/7/9
 */
@RestController
@RequestMapping(value = "sanyi")
public class SanYiController {

    @Resource
    private SanYiService sanYiService;

    @PostMapping(value = "find-hotel")
    public void findHotel() {
        try {
            sanYiService.findHotel();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "find-city")
    public void findCity() {
        try {
            sanYiService.findCity();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "find-hotel2")
    public void findHotel2() {
        try {
            sanYiService.findHotel2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "export")
    public void export() {
        try {
            sanYiService.export();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
