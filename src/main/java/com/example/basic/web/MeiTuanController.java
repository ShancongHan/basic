package com.example.basic.web;

import com.example.basic.service.MeiTuanService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 美团匹配
 * @author han
 * @date 2024/11/11
 */
@RestController
@RequestMapping(value = "meituan")
public class MeiTuanController {

    @Resource
    private MeiTuanService meiTuanService;

    /**
     * 比较东呈酒店
     */
    @PostMapping(value = "match/dongcheng")
    public void matchDongcheng() {
        try {
            meiTuanService.matchDongcheng();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 导出东呈匹配结果
     */
    @PostMapping(value = "export/dongcheng")
    public void exportDongcheng() {
        try {
            meiTuanService.exportDongcheng();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 导出东呈待匹配记录
     */
    @PostMapping(value = "export/dongcheng2")
    public void exportDongcheng2() {
        try {
            meiTuanService.exportDongcheng2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 比较华住酒店
     */
    @PostMapping(value = "match/huazhu")
    public void matchHuazhu() {
        try {
            meiTuanService.matchHuazhu();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 比较即将酒店
     */
    @PostMapping(value = "match/jinjiang")
    public void matchJinjiang() {
        try {
            meiTuanService.matchJinjiang();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 比较红色加力酒店
     */
    @PostMapping(value = "match/hsjl")
    public void matchHsjl() {
        try {
            meiTuanService.matchHsjl();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 比较红色加力酒店2
     */
    @PostMapping(value = "match/hsjl2")
    public void matchHsjl2() {
        try {
            meiTuanService.matchHsjl2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 导入国内城市和区数据
     */
    @PostMapping(value = "import/city")
    public void importCity() {
        try {
            meiTuanService.importCity();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 初版数据
     */
    @PostMapping(value = "init/hotel")
    public void initHotel() {
        try {
            meiTuanService.initHotel();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 初版城市区域数据
     */
    @PostMapping(value = "init/city")
    public void initCity() {
        try {
            meiTuanService.initCity();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 初版图片数据
     */
    @PostMapping(value = "init/image")
    public void initImage() {
        try {
            meiTuanService.initImage();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 初版政策数据
     */
    @PostMapping(value = "init/policy")
    public void initPolicy() {
        try {
            meiTuanService.initPolicy();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 初版设施数据
     */
    @PostMapping(value = "init/facilities")
    public void initFacilities() {
        try {
            meiTuanService.initFacilities();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 初版统计数据
     */
    @PostMapping(value = "init/statistics")
    public void initStatistics() {
        try {
            meiTuanService.initStatistics();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 导出集团酒店信息
     */
    @PostMapping(value = "export")
    public void export() {
        try {
            meiTuanService.export();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店停车场数据
     */
    @PostMapping(value = "analyze/hotel-parking-policy")
    public void analyzeHotelParkingPolicy() {
        try {
            meiTuanService.analyzeHotelParkingPolicy();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店充电桩数据
     */
    @PostMapping(value = "analyze/hotel-charge-point-policy")
    public void analyzeHotelChargePolicy() {
        try {
            meiTuanService.analyzeHotelChargePolicy();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店设施服务入库
     */
    @PostMapping(value = "analyze/hotel-service-and-facilities")
    public void analyzeHotelSomething() {
        try {
            meiTuanService.analyzeHotelSomething();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店设施服务入库2
     */
    @PostMapping(value = "analyze/hotel-service-and-facilities2")
    public void analyzeHotelSomething2() {
        try {
            meiTuanService.analyzeHotelSomething2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店房间设施
     */
    @PostMapping(value = "analyze/hotel-room-facilities")
    public void analyzeHotelRoomFacilities() {
        try {
            meiTuanService.analyzeHotelRoomFacilities();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
