package com.example.basic.web;

import com.example.basic.service.ExpediaService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * expedia接口
 * @author han
 * @date 2024/7/30
 */
@RestController
@RequestMapping(value = "expedia")
public class ExpediaController {

    @Resource
    private ExpediaService expediaService;

    /**
     * 保存大洲
     */
    @PostMapping(value = "continent")
    public void continent() {
        try {
            expediaService.continent();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 保存国家
     */
    @PostMapping(value = "country")
    public void country() {
        try {
            expediaService.country();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 获取区域
     */
    @PostMapping(value = "pull-regions")
    public void pullRegions() {
        try {
            expediaService.pullRegions();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 获取酒店
     */
    @PostMapping(value = "pull-content")
    public void pullContent(Integer page, String url) {
        try {
            expediaService.pullContent(page, url);
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析静态文件
     */
    @PostMapping(value = "analyze-static-file")
    public void analyzeStaticFile() {
        try {
            expediaService.analyzeStaticFile();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 完善区域
     *
     * @param page page
     * @param url   url
     */
    @PostMapping(value = "finish-regions")
    public void finishRegions(Integer page, String url) {
        try {
            expediaService.finishRegions(page, url);
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 获取连锁和品牌信息
     */
    @PostMapping(value = "pull-chain")
    public void pullChain() {
        try {
            expediaService.pullChain();
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
            expediaService.analyzePropertyStaticFile();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店基础信息静态文件2
     */
    @PostMapping(value = "analyze-property-static-file2")
    public void analyzePropertyStaticFile2() {
        try {
            expediaService.analyzePropertyStaticFile2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
