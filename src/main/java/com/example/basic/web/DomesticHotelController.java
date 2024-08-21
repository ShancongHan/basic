package com.example.basic.web;

import com.example.basic.service.DomesticHotelService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 国内酒店
 *
 * @author han
 * @date 2024/8/16
 */
@RestController
@RequestMapping(value = "domestic")
public class DomesticHotelController {

    @Resource
    private DomesticHotelService domesticHotelService;

    /**
     * 检测国内匹配问题数据
     */
    @PostMapping(value = "check-old-data")
    public void checkOldData() {
        try {
            domesticHotelService.checkOldData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测国内没有映射的基础数据
     */
    @PostMapping(value = "check-basic-data")
    public void checkBasicData() {
        try {
            domesticHotelService.checkBasicData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测elong的映射数据
     */
    @PostMapping(value = "check-elong-data")
    public void checkElongData() {
        try {
            domesticHotelService.checkElongData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新elong的映射字段
     */
    @PostMapping(value = "update-elong-reflect")
    public void updateElongReflect() {
        try {
            domesticHotelService.updateElongReflect();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测meituan的映射数据
     */
    @PostMapping(value = "check-meituan-data")
    public void checkMeituanData() {
        try {
            domesticHotelService.checkMeituanData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新Meituan的映射字段
     */
    @PostMapping(value = "update-meituan-reflect")
    public void updateMeituanReflect() {
        try {
            domesticHotelService.updateMeituanReflect();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测Qiantao的映射数据
     */
    @PostMapping(value = "check-qiantao-data")
    public void checkQiantaoData() {
        try {
            domesticHotelService.checkQiantaoData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新Qiantao的映射字段
     */
    @PostMapping(value = "update-qiantao-reflect")
    public void updateQiantaoReflect() {
        try {
            domesticHotelService.updateQiantaoReflect();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测hsjl的映射数据
     */
    @PostMapping(value = "check-hsjl-data")
    public void checkHsjlData() {
        try {
            domesticHotelService.checkHsjlData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新hsjl的映射字段
     */
    @PostMapping(value = "update-hsjl-reflect")
    public void updateHsjlReflect() {
        try {
            domesticHotelService.updateHsjlReflect();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测daolv的映射数据
     */
    @PostMapping(value = "check-daolv-data")
    public void checkDaolvData() {
        try {
            domesticHotelService.checkDaolvData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新daolv的映射字段
     */
    @PostMapping(value = "update-daolv-reflect")
    public void updateDaolvReflect() {
        try {
            domesticHotelService.updateDaolvReflect();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测huazhu的映射数据
     */
    @PostMapping(value = "check-huazhu-data")
    public void checkHuazhuData() {
        try {
            domesticHotelService.checkHuazhuData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新huazhu的映射字段
     */
    @PostMapping(value = "update-huazhu-reflect")
    public void updateHuazhuReflect() {
        try {
            domesticHotelService.updateHuazhuReflect();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测dongcheng的映射数据
     */
    @PostMapping(value = "check-dongcheng-data")
    public void checkDongchengData() {
        try {
            domesticHotelService.checkDongchengData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新dongcheng的映射字段
     */
    @PostMapping(value = "update-dongcheng-reflect")
    public void updateDongchengReflect() {
        try {
            domesticHotelService.updateDongchengReflect();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测jinjiang的映射数据
     */
    @PostMapping(value = "check-jinjiang-data")
    public void checkJinjiangData() {
        try {
            domesticHotelService.checkJinjiangData();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新jinjiang的映射字段
     */
    @PostMapping(value = "update-jinjiang-reflect")
    public void updateJinjiangReflect() {
        try {
            domesticHotelService.updateJinjiangReflect();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
