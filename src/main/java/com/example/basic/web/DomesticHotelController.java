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
}
