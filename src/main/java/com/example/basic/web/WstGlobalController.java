package com.example.basic.web;

import com.example.basic.service.WstGlobalService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * wst国际酒店
 *
 * @author han
 * @date 2024/12/19
 */
@RestController
@RequestMapping(value = "wst/global/hotel")
public class WstGlobalController {

    @Resource
    private WstGlobalService wstGlobalService;

    /**
     * 国际酒店分类字典
     */
    @PostMapping(value = "category-dictionary")
    public void categoryDictionary() {
        try {
            wstGlobalService.categoryDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店主题字典
     */
    @PostMapping(value = "themes-dictionary")
    public void themesDictionary() {
        try {
            wstGlobalService.themesDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店统计字典
     */
    @PostMapping(value = "statistics-dictionary")
    public void statisticsDictionary() {
        try {
            wstGlobalService.statisticsDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店图片字典
     */
    @PostMapping(value = "images-dictionary")
    public void imagesDictionary() {
        try {
            wstGlobalService.imagesDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店语言字典
     */
    @PostMapping(value = "spoken_languages-dictionary")
    public void spokenLanguagesDictionary() {
        try {
            wstGlobalService.spokenLanguagesDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店房景字典
     */
    @PostMapping(value = "room_views-dictionary")
    public void roomViewsDictionary() {
        try {
            wstGlobalService.roomViewsDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
