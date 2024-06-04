package com.example.basic.web;

import com.example.basic.service.DataService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author han
 * @date 2024/4/12
 */
@RestController
@RequestMapping(value = "data")
public class DataController {

    @Resource
    private DataService dataService;

    @PostMapping(value = "analyze")
    public void analyze() {
        try {
            dataService.analyze();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
