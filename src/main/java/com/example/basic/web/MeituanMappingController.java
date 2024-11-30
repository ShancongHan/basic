package com.example.basic.web;

import com.example.basic.service.MeituanMappingService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author han
 * @date 2024/11/27
 */
@RestController
@RequestMapping(value = "meituan")
public class MeituanMappingController {

    @Resource
    private MeituanMappingService meituanMappingService;


    /**
     * 导入美团和红色加力映射
     */
    @PostMapping(value = "hsjl/mapping")
    public void hsjlMapping() {
        try {
            meituanMappingService.hsjlMapping();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
