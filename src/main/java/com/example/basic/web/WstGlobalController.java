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

    /**
     * 国际酒店房间设施字典
     */
    @PostMapping(value = "amenities_rooms-dictionary")
    public void amenitiesRoomsDictionary() {
        try {
            wstGlobalService.amenitiesRoomsDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店设施字典
     */
    @PostMapping(value = "amenities_property-dictionary")
    public void amenitiesPropertyDictionary() {
        try {
            wstGlobalService.amenitiesPropertyDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
    /**
     * 国际酒店现付支付方式字典
     */
    @PostMapping(value = "onsite_payment-dictionary")
    public void onsitePaymentDictionary() {
        try {
            wstGlobalService.onsitePaymentDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店设施费率字典
     */
    @PostMapping(value = "amenities_rates-dictionary")
    public void amenitiesRatesDictionary() {
        try {
            wstGlobalService.amenitiesRatesDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店宠物字典
     */
    @PostMapping(value = "pets-dictionary")
    public void petsDictionary() {
        try {
            wstGlobalService.petsDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店通用属性字典
     */
    @PostMapping(value = "general-dictionary")
    public void generalDictionary() {
        try {
            wstGlobalService.generalDictionary();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 国际酒店连锁
     */
    @PostMapping(value = "chain_brand")
    public void chainBrand() {
        try {
            wstGlobalService.chainBrand();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析20241225下载的酒店基础信息文件
     */
    @PostMapping(value = "analyze-hotel-file")
    public void analyzeHotelFile() throws Exception {
        wstGlobalService.analyzeHotelFile();
    }

    /**
     * 拉取酒店英文信息
     */
    @PostMapping(value = "pull-hotel-en")
    public void pullHotelEn() throws Exception {
        wstGlobalService.pullHotelEn();
    }

    /**
     * 拉取酒店中文信息
     */
    @PostMapping(value = "pull-hotel")
    public void pullHotel() throws Exception {
        wstGlobalService.pullHotel();
    }

    /**
     * 完善区域数据
     */
    @PostMapping(value = "complete-region")
    public void completeRegion() throws Exception {
        wstGlobalService.completeRegion();
    }
}
