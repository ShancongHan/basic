package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.basic.domain.Hotel;
import com.example.basic.entity.JdGlobalGeo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author han
 * @date 2024/4/30
 */
@Slf4j
@Service
public class HotelDataAnalyzeService {

    public void analyze() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\酒店同步数据\\HotelSummary.csv";
        InputStream inputStream = new FileInputStream(fileName);
        EasyExcel.read(inputStream, Hotel.class, new PageReadListener<Hotel>(dataList -> {
            for (Hotel oneLine : dataList) {
                log.info("读取到一条数据{}", oneLine);
            }
        }, 1000)).headRowNumber(1).sheet().doRead();
    }



}
