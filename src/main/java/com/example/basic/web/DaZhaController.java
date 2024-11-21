package com.example.basic.web;

import com.example.basic.service.DaZhaService;
import com.example.basic.service.DataService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打杂
 *
 * @author han
 * @date 2024/10/9
 */
@RestController
@RequestMapping(value = "dazha")
public class DaZhaController {

    @Resource
    private DaZhaService daZhaService;

    /**
     * 比较国家
     */
    @PostMapping(value = "country")
    public void analyze() {
        try {
            daZhaService.country();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
