package com.example.basic.web;

import com.example.basic.service.AnalyzeHotelDataService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 酒店数据分析
 * @author han
 * @date 2024/9/2
 */
@RestController
@RequestMapping(value = "analyze")
public class AnalyzeHotelDataController {

    @Resource
    private AnalyzeHotelDataService analyzeHotelDataService;

    /**
     * 道旅酒店数据分析
     */
    @PostMapping(value = "daolv")
    public void daolv() {
        analyzeHotelDataService.daolv();
    }

    /**
     * 更新webbeds酒店映射状态
     */
    @PostMapping(value = "webbeds")
    public void webbeds() {
        analyzeHotelDataService.webbeds();
    }

    /**
     * webbeds酒店找不到的数据分析
     */
    @PostMapping(value = "webbeds-not-found")
    public void webbedsNotFound() {
        analyzeHotelDataService.webbedsNotFound();
    }

    /**
     * webbeds酒店导出
     */
    @PostMapping(value = "webbeds-export")
    public void webbedsExport() {
        analyzeHotelDataService.webbedsExport();
    }
}
