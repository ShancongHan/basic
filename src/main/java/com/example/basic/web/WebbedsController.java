package com.example.basic.web;

import com.example.basic.service.WebbedsService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author han
 * @date 2024/5/28
 */
@RestController
@RequestMapping(value = "webbeds")
public class WebbedsController {

    @Resource
    private WebbedsService webbedsService;

    @PostMapping(value = "import-data")
    public void importData() {
        try {
            webbedsService.importData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "import-mapping")
    public void importMapping() {
        try {
            webbedsService.importMapping();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "auto-match")
    public void autoMatch() {
        try {
            webbedsService.autoMatch();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "match-test")
    public void matchTest() {
        try {
            webbedsService.matchTest();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "match-test2")
    public void matchTest2() {
        try {
            webbedsService.matchTest2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "match-test3")
    public void matchTest3() {
        try {
            webbedsService.matchTest3();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    @PostMapping(value = "match-test4")
    public void matchTest4() {
        try {
            webbedsService.matchTest4();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
