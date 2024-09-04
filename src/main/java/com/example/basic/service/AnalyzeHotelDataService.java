package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.example.basic.dao.JdJdbGjDao;
import com.example.basic.dao.WebbedsHotelDataDao;
import com.example.basic.dao.ZhJdJdbGjMappingDao;
import com.example.basic.domain.HotelExport;
import com.example.basic.domain.HotelExport2;
import com.example.basic.domain.Webbeds08232Bean2;
import com.example.basic.domain.WebbedsImportBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author han
 * @date 2024/9/2
 */
@Slf4j
@Service
public class AnalyzeHotelDataService {

    @Resource
    private ZhJdJdbGjMappingDao zhJdJdbGjMappingDao;

    @Resource
    private JdJdbGjDao jdJdbGjDao;

    @Resource
    private WebbedsHotelDataDao webbedsHotelDataDao;

    public void daolv() {
        List<Long> localIds = jdJdbGjDao.selectAllIds();
        List<Long> daolvIds = zhJdJdbGjMappingDao.selectDaolvIds();

        Map<Long, String> map = Maps.newHashMapWithExpectedSize(localIds.size());
        for (Long localId : daolvIds) {
            map.put(localId, "1");
        }
        List<Long> notFoundList = Lists.newArrayList();
        for (Long daolvId : localIds) {
            if (!map.containsKey(daolvId)) {
                notFoundList.add(daolvId);
            }
        }
        log.info("找不到的个数为{}, 具体列表为{}", notFoundList.size(), notFoundList);
    }

    public void webbeds() {
        List<Long> webbedsIds = zhJdJdbGjMappingDao.selectWebbedsIds();
        int start = 0;
        for (int j = 0; j < webbedsIds.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<Long> list = webbedsIds.subList(start, j);
                webbedsHotelDataDao.updateSaleByIds(list);
                start = j;
            }
        }
        List<Long> list = webbedsIds.subList(start, webbedsIds.size());
        if (CollectionUtils.isNotEmpty(list)) {
            webbedsHotelDataDao.updateSaleByIds(list);
        }
    }

    public void webbedsNotFound() {
        List<Long> webbedsIds = webbedsHotelDataDao.selectSaleIds();
        List<Long> webbedsIds1 = zhJdJdbGjMappingDao.selectWebbedsIds();
        log.info("webbeds sale 长度：{}， 去除重复的长度：{}", webbedsIds1.size(), webbedsIds1.stream().distinct().count());

        Map<Long, String> map = Maps.newHashMapWithExpectedSize(webbedsIds.size());
        for (Long localId : webbedsIds) {
            map.put(localId, "1");
        }
        List<Long> notFoundList = Lists.newArrayList();
        for (Long id : webbedsIds1) {
            if (!map.containsKey(id)) {
                notFoundList.add(id);
            }
        }
        log.info("找不到的个数为{}, 具体列表为{}", notFoundList.size(), notFoundList);
    }

    public void webbedsExport() {
        List<HotelExport2> webbedsIds = webbedsHotelDataDao.selectExport();
        String file = "C:\\wst_han\\打杂\\webbeds\\webbeds-0902映射.xlsx";
        EasyExcel.write(file, HotelExport2.class).sheet("webbeds酒店映射").doWrite(webbedsIds);
    }
}
