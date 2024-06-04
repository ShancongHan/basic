package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.basic.dao.JdGlobalGeoDao;
import com.example.basic.entity.JdGlobalGeo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author han
 * @date 2024/5/9
 */
@Slf4j
@Service
public class JdGlobalGeoService {

    @Resource
    private JdGlobalGeoDao jdGlobalGeoDao;

    public void basicData() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\基础数据\\业务提供-行政区划.xls";
        InputStream inputStream = new FileInputStream(fileName);
        EasyExcel.read(inputStream, JdGlobalGeo.class, new PageReadListener<JdGlobalGeo>(dataList -> {
            if (CollectionUtils.isNotEmpty(dataList)) {
                log.info("保存数据长度{}",dataList.size());
                jdGlobalGeoDao.saveBatch(dataList);
            }
        }, 1000)).headRowNumber(0).sheet(9).doRead();
        /*int first = 1;
        for (int i = 0; i < 10; i++) {
            EasyExcel.read(inputStream, JdGlobalGeo.class, new PageReadListener<JdGlobalGeo>(dataList -> {
                if (CollectionUtils.isNotEmpty(dataList)) {
                    log.info("保存数据长度{}",dataList.size());
                    jdGlobalGeoDao.saveBatch(dataList);
                }
            }, 1000)).headRowNumber(first).sheet(i).doRead();
            first = 0;
        }*/
    }
}
