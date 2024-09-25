package com.example.basic.web;

import com.example.basic.service.ExpediaService;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * expedia接口
 *
 * @author han
 * @date 2024/7/30
 */
@RestController
@RequestMapping(value = "expedia")
public class ExpediaController {

    @Resource
    private ExpediaService expediaService;

    /**
     * 保存大洲
     */
    @PostMapping(value = "continent")
    public void continent() {
        try {
            expediaService.continent();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 保存国家
     */
    @PostMapping(value = "country")
    public void country() {
        try {
            expediaService.country();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 获取区域
     */
    @PostMapping(value = "pull-regions")
    public void pullRegions() {
        try {
            expediaService.pullRegions();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 获取酒店
     */
    @PostMapping(value = "pull-content")
    public void pullContent(Integer page, String url) {
        try {
            expediaService.pullContent(page, url);
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析静态文件
     */
    @PostMapping(value = "analyze-static-file")
    public void analyzeStaticFile() {
        try {
            expediaService.analyzeStaticFile();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 完善区域
     *
     * @param page page
     * @param url  url
     */
    @PostMapping(value = "finish-regions")
    public void finishRegions(Integer page, String url) {
        try {
            expediaService.finishRegions(page, url);
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 获取连锁和品牌信息
     */
    @PostMapping(value = "pull-chain")
    public void pullChain() {
        try {
            expediaService.pullChain();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店基础信息静态文件
     */
    @PostMapping(value = "analyze-property-static-file")
    public void analyzePropertyStaticFile() {
        try {
            expediaService.analyzePropertyStaticFile();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析酒店基础信息静态文件2
     */
    @PostMapping(value = "analyze-property-static-file2")
    public void analyzePropertyStaticFile2() {
        try {
            expediaService.analyzePropertyStaticFile2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 完善酒店名称
     */
    @PostMapping(value = "finish-property")
    public void finishProperty() {
        try {
            expediaService.finishProperty();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 匹配expedia和道旅酒店
     */
    @PostMapping(value = "mapping")
    public void mapping() {
        try {
            expediaService.mapping();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 计算expedia可以立即上线酒店数
     */
    @PostMapping(value = "expedia-statistics")
    public void expediaStatistics() {
        try {
            expediaService.expediaStatistics();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }


    /**
     * 解析expedia英文酒店文件
     */
    @PostMapping(value = "analyze-property-static-file3")
    public void analyzePropertyStaticFile3() throws Exception {
        expediaService.analyzePropertyStaticFile3();
    }

    /**
     * 获取酒店详情
     */
    @PostMapping(value = "pull-content-detail")
    public void pullContentDetail() {
        expediaService.pullContentDetail();
    }

    /**
     * 获取酒店价格
     */
    @PostMapping(value = "pull-content-price")
    public void pullContentPrice() {
        expediaService.pullContentPrice();
    }

    /**
     * 统计第一版上线的酒店
     */
    @PostMapping(value = "expedia-v1-up")
    public void expediaV1Up() {
        try {
            expediaService.expediaV1Up();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 检测不同国家价格是否一致
     */
    @PostMapping(value = "check-multi-price")
    public void checkMultiPrice() {
        try {
            expediaService.checkMultiPrice();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 校验国家
     */
    @PostMapping(value = "pull-check-country")
    public void pullCheckCountry() {
        try {
            expediaService.pullCheckCountry();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 获取区域v2
     */
    @PostMapping(value = "pull-regions-v2")
    public void pullRegionsV2() {
        try {
            expediaService.pullRegionsV2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 补充国家信息
     */
    @PostMapping(value = "finish-country")
    public void finishCountry() {
        try {
            expediaService.finishCountry();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 补充区域信息
     */
    @PostMapping(value = "finish-regions-v2")
    public void finishRegionsV2() {
        try {
            expediaService.finishRegionsV2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 查询区域下酒店
     */
    @PostMapping(value = "pull-property")
    public void pullProperty() {
        try {
            expediaService.pullProperty();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 解析重要推荐酒店
     */
    @PostMapping(value = "analyze-property")
    public void analyzeProperty() {
        try {
            expediaService.analyzeProperty();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 更新已匹配16分酒店
     */
    @PostMapping(value = "match")
    public void match() {
        try {
            expediaService.match();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 新酒店id
     */
    @PostMapping(value = "new-hotel")
    public void newHotel() {
        try {
            expediaService.newHotel();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 增加新酒店
     */
    @PostMapping(value = "add-hotel")
    public void addHotel() {
        try {
            expediaService.addHotel();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 比较酒店列表
     */
    @PostMapping(value = "compare-hotel")
    public void compareHotel() {
        try {
            expediaService.compareHotel();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 导出第一版主推酒店
     */
    @PostMapping(value = "export-v1-hotel")
    public void exportHotel() {
        try {
            expediaService.exportHotel();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 导出第一版主推酒店2
     */
    @PostMapping(value = "export-v1-hotel2")
    public void exportHotel2() {
        try {
            expediaService.exportHotel2();
        } catch (Exception e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
    }
}