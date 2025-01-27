package com.example.basic.web;

import com.example.basic.service.DidaService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 道旅
 * @author han
 * {@code @date} 2025/1/26
 */
@RestController
@RequestMapping(value = "dida")
public class DidaController {

    @Resource
    private DidaService didaService;

    /**
     * 拉取国家
     */
    @PostMapping(value = "pull-country")
    public void pullCountry() {
        try {
            didaService.pullCountry();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店静态文件
     */
    @PostMapping(value = "analyze-hotel-info")
    public void analyzeHotelInfo() {
        try {
            didaService.analyzeHotelInfo();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 拉取酒店信息
     */
    @PostMapping(value = "pull-hotel-info")
    public void pullHotelInfo() {
        try {
            didaService.pullHotelInfo();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
