package com.example.basic.web;

import com.example.basic.service.XcServiceImpl;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 携程映射
 *
 * @author han
 * @date 2024/12/4
 */
@RestController
@RequestMapping(value = "xc")
public class XcController {

    @Resource
    private XcServiceImpl xcService;

    /**
     * 匹配集团
     */
    @PostMapping(value = "match-group")
    public void matchGroup() {
        try {
            xcService.matchGroup();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 匹配品牌
     */
    @PostMapping(value = "match-brand")
    public void matchBrand() {
        try {
            xcService.matchBrand();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 匹配地标
     */
    @PostMapping(value = "match-zone")
    public void matchZone() {
        try {
            xcService.matchZone();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * xxxxx
     */
    @PostMapping(value = "xxx")
    public void xxxx() {
        try {
            xcService.xxxx();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}
