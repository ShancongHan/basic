package com.example.basic.web;

import com.example.basic.service.ExpediaService2;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * EPS重新拉取
 * @author han
 * @date 2024/12/17
 */
@RestController
@RequestMapping(value = "new/expedia")
public class ExpediaController2 {

    @Resource
    private ExpediaService2 expediaService2;

    /**
     * 按照国家获取区域
     */
    @PostMapping(value = "pull-regions")
    public void pullRegions() {
        try {
            expediaService2.pullRegions();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 补充区域信息
     */
    @PostMapping(value = "finish-regions")
    public void finishRegions() {
        try {
            expediaService2.finishRegions();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店基础信息静态文件
     */
    @PostMapping(value = "analyze-property-static-file")
    public void analyzePropertyStaticFile() {
        try {
            expediaService2.analyzePropertyStaticFile();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
