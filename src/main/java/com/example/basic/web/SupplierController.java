package com.example.basic.web;

import com.example.basic.service.SupplierService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供应商
 * @author han
 * @date 2024/8/22
 */
@RestController
@RequestMapping(value = "supplier")
public class SupplierController {

    @Resource
    private SupplierService supplierService;

    /**
     * 获取东城酒店信息
     */
    @GetMapping(value = "dc")
    public void dcHotel() {
        supplierService.dcHotel();
    }

    /**
     * 校验东城酒店信息
     */
    @GetMapping(value = "dc/check")
    public void dcHotelCheck() {
        supplierService.dcHotelCheck();
    }

    /**
     * 比对0826东呈数据
     */
    @GetMapping(value = "dc/match")
    public void dcHotelMatch() {
        supplierService.dcHotelMatch();
    }

    /**
     * 导出id
     */
    @GetMapping(value = "dc/xxx")
    public void xxx() {
        supplierService.xxx();
    }
}
