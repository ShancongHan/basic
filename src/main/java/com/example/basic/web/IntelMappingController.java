package com.example.basic.web;

import com.example.basic.service.IntelMappingService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 国际mapping解析入库
 * @author han
 * {@code @date} 2025/1/21
 */
@RestController
@RequestMapping(value = "intel-mapping")
public class IntelMappingController {

    @Resource
    private IntelMappingService intelMappingService;

    /**
     * webbeds mapping 解析入库
     */
    @PostMapping(value = "webbeds")
    public void webbeds() {
        try {
            intelMappingService.webbeds();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * dida mapping 解析入库
     */
    @PostMapping(value = "dida")
    public void dida() {
        try {
            intelMappingService.dida();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
