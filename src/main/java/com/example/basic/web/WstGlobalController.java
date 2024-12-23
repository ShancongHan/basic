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
}
