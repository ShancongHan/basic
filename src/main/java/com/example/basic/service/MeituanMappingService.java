package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.basic.dao.MtHsjlMappingDao;
import com.example.basic.domain.MainHotelImport;
import com.example.basic.entity.MtHsjlMapping;
import com.example.basic.entity.WstHotelImages;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * @author han
 * @date 2024/11/27
 */
@Service
public class MeituanMappingService {

    @Resource
    private MtHsjlMappingDao mtHsjlMappingDao;


    public void hsjlMapping() throws FileNotFoundException {
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\国内匹配酒店\\美团id.xlsx";
        EasyExcel.read(fileName);
        InputStream inputStream = new FileInputStream(fileName);
        List<MtHsjlMapping> imports = Lists.newArrayListWithCapacity(2 << 17);
        EasyExcel.read(inputStream, MtHsjlMapping.class, new PageReadListener<MtHsjlMapping>(imports::addAll, 1000)).headRowNumber(1).sheet().doRead();
        imports.forEach(e->e.setMtId(Long.valueOf(e.getMtIdString().replaceAll("'",""))));
        saveBatch(imports);
    }

    private void saveBatch(List<MtHsjlMapping> imports) {
        if (CollectionUtils.isEmpty(imports)) return;
        int start = 0;
        for (int j = 0; j < imports.size(); j++) {
            if (j != 0 && j % 5000 == 0) {
                List<MtHsjlMapping> list = imports.subList(start, j);
                mtHsjlMappingDao.saveBatch(list);
                start = j;
            }
        }
        List<MtHsjlMapping> list = imports.subList(start, imports.size());
        if (CollectionUtils.isNotEmpty(list)) {
            mtHsjlMappingDao.saveBatch(list);
        }
    }
}
