package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.basic.dao.WstHotelGlobalInfoDao;
import com.example.basic.domain.mapping.DidaExpediaMapping;
import com.example.basic.domain.mapping.WebbedsExpediaMapping;
import com.example.basic.entity.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2025/1/21
 */
@Slf4j
@Service
public class IntelMappingService {

    @Resource
    private WstHotelGlobalInfoDao wstHotelGlobalInfoDao;

    private static final Executor executor = new ThreadPoolExecutor(50, 100,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(700000));

    public void webbeds() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\国际酒店匹配\\拿回来的\\11月全量(EPS mapping).xlsx";
        EasyExcel.read(fileName);
        InputStream inputStream = new FileInputStream(fileName);
        List<WebbedsExpediaMapping> webbedsExpediaMappingList = Lists.newArrayListWithCapacity(350000);
        EasyExcel.read(inputStream, WebbedsExpediaMapping.class, new PageReadListener<WebbedsExpediaMapping>(webbedsExpediaMappingList::addAll, 1000)).headRowNumber(1).sheet().doRead();
        Map<String, List<WebbedsExpediaMapping>> expediaWebbedsListMap = webbedsExpediaMappingList.stream()
                .filter(e -> !"无".equals(e.getExpediaId())).collect(Collectors.groupingBy(WebbedsExpediaMapping::getExpediaId));
        List<WstHotelGlobalInfo> wstHotelGlobalInfos = wstHotelGlobalInfoDao.selectAllId();
        Map<String, List<WstHotelGlobalInfo>> expediaIdMap = wstHotelGlobalInfos.stream().collect(Collectors.groupingBy(WstHotelGlobalInfo::getExpediaId));

        Set<String> webbedsRepeats = Sets.newHashSet();
        expediaWebbedsListMap.forEach((k, v)-> {
            if (v.size() > 1) {
                webbedsRepeats.add(k);
            }
        });
        List<WstHotelGlobalInfo> updates = Lists.newArrayListWithCapacity(webbedsExpediaMappingList.size() - webbedsRepeats.size());
        for (WebbedsExpediaMapping webbedsExpediaMapping : webbedsExpediaMappingList) {
            String expediaId = webbedsExpediaMapping.getExpediaId();
            if (webbedsRepeats.contains(expediaId)) {
                continue;
            }
            List<WstHotelGlobalInfo> list = expediaIdMap.get(expediaId);
            if (CollectionUtils.isNotEmpty(list)) {
                WstHotelGlobalInfo wstHotelGlobalInfo = list.get(0);
                if (wstHotelGlobalInfo != null) {
                    WstHotelGlobalInfo info = new WstHotelGlobalInfo();
                    info.setHotelId(wstHotelGlobalInfo.getHotelId());
                    String webbedsId = webbedsExpediaMapping.getWebbedsId();
                    info.setWebbedsId(Long.valueOf(webbedsId));
                    updates.add(info);
                }
            }
        }
        log.info("webbeds解析出excel记录{}条，转换成map后{}条, 更新到数据库{}条", webbedsExpediaMappingList.size()
                , expediaWebbedsListMap.size(), updates.size());
        log.info("webbeds重复的expedia列表有{}条，分别是：{} ", webbedsRepeats.size(), String.join(",", webbedsRepeats));
        for (WstHotelGlobalInfo update : updates) {
            CompletableFuture.runAsync(() -> wstHotelGlobalInfoDao.updateWebbedsId(update), executor);
        }
    }

    public void dida() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\国际酒店匹配\\拿回来的\\EPS和dida匹配关系forWSTLY0108.xlsx";
        EasyExcel.read(fileName);
        InputStream inputStream = new FileInputStream(fileName);
        List<DidaExpediaMapping> didaExpediaMappingList = Lists.newArrayListWithCapacity(550000);
        EasyExcel.read(inputStream, DidaExpediaMapping.class, new PageReadListener<DidaExpediaMapping>(didaExpediaMappingList::addAll, 1000)).headRowNumber(2).sheet().doRead();
        Map<String, List<DidaExpediaMapping>> expediaDidaListMap = didaExpediaMappingList.stream().collect(Collectors.groupingBy(DidaExpediaMapping::getExpediaId));
        List<WstHotelGlobalInfo> wstHotelGlobalInfos = wstHotelGlobalInfoDao.selectAllId();
        Map<String, List<WstHotelGlobalInfo>> expediaIdMap = wstHotelGlobalInfos.stream().collect(Collectors.groupingBy(WstHotelGlobalInfo::getExpediaId));

        Set<String> didaRepeats = Sets.newHashSet();
        expediaDidaListMap.forEach((k, v)-> {
            if (v.size() > 1) {
                didaRepeats.add(k);
            }
        });
        List<WstHotelGlobalInfo> updates = Lists.newArrayListWithCapacity(didaExpediaMappingList.size() - didaRepeats.size());
        for (DidaExpediaMapping didaExpediaMapping : didaExpediaMappingList) {
            String expediaId = didaExpediaMapping.getExpediaId();
            if (didaRepeats.contains(expediaId)) {
                continue;
            }
            List<WstHotelGlobalInfo> list = expediaIdMap.get(expediaId);
            if (CollectionUtils.isNotEmpty(list)) {
                WstHotelGlobalInfo wstHotelGlobalInfo = list.get(0);
                if (wstHotelGlobalInfo != null) {
                    WstHotelGlobalInfo info = new WstHotelGlobalInfo();
                    info.setHotelId(wstHotelGlobalInfo.getHotelId());
                    String didaId = didaExpediaMapping.getDidaId();
                    info.setDidaId(Long.valueOf(didaId));
                    updates.add(info);
                }
            }
        }
        log.info("dida解析出excel记录{}条，转换成map后{}条, 更新到数据库{}条", didaExpediaMappingList.size()
                , expediaDidaListMap.size(), updates.size());
        log.info("dida重复的expedia列表有{}条，分别是：{} ", didaRepeats.size(), String.join(",", didaRepeats));
        for (WstHotelGlobalInfo update : updates) {
            CompletableFuture.runAsync(() -> wstHotelGlobalInfoDao.updateDidaId(update), executor);
        }
    }
}
