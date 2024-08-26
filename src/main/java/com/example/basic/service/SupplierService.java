package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.dao.SupplierDongChengDao;
import com.example.basic.domain.*;
import com.example.basic.entity.SupplierDongCheng;
import com.example.basic.utils.HttpUtils;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author han
 * @date 2024/8/22
 */
@Slf4j
@Service
public class SupplierService {

    @Resource
    private SupplierDongChengDao supplierDongChengDao;

    @Resource
    private HttpUtils httpUtils;

    public void dcHotel() {
        DongChengGnResponse response = httpUtils.getDongChengCity();
        String data = response.getData();
        List<DongChengCity> dongChengCities = JSON.parseArray(data, DongChengCity.class);
        System.out.println(dongChengCities.size());
        //List<DongChengHotel> hotels = Lists.newArrayListWithCapacity(5000);
        for (DongChengCity dongChengCity : dongChengCities) {
            Integer cityId = dongChengCity.getCityID();
            getOneCityHotels(cityId);
        }
        //System.out.println(hotels.size());
        /*List<SupplierDongCheng> supplierDongChengs = parse(hotels);
        int start = 0;
        for (int j = 0; j < supplierDongChengs.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<SupplierDongCheng> list = supplierDongChengs.subList(start, j);
                supplierDongChengDao.saveBatch(list);
                start = j;
            }
        }
        List<SupplierDongCheng> list = supplierDongChengs.subList(start, supplierDongChengs.size());
        if (CollectionUtils.isNotEmpty(list)) {
            supplierDongChengDao.saveBatch(list);
        }*/
    }

    private List<SupplierDongCheng> parse(List<DongChengHotel> hotels) {
        List<SupplierDongCheng> supplierDongChengs = Lists.newArrayListWithCapacity(hotels.size());
        Date now = new Date();
        for (DongChengHotel hotel : hotels) {
            SupplierDongCheng supplierDongCheng = new SupplierDongCheng();
            supplierDongCheng.setHotelId(hotel.getHotelId());
            supplierDongCheng.setCityName(hotel.getCityName());
            supplierDongCheng.setProvinceName(hotel.getProvinceName());
            DongChengHotelBaseInfo baseInfo = hotel.getBaseInfo();
            supplierDongCheng.setCityId(baseInfo.getCityid());
            supplierDongCheng.setJdmc(baseInfo.getName());
            supplierDongCheng.setJdmcEn(baseInfo.getPinyin());
            supplierDongCheng.setJddz(baseInfo.getAddr());
            supplierDongCheng.setJddh(baseInfo.getTel());
            supplierDongCheng.setPicUrl(baseInfo.getPic());
            supplierDongCheng.setLat(new BigDecimal(baseInfo.getLatitude().trim()));
            supplierDongCheng.setLon(new BigDecimal(baseInfo.getLongitude().trim()));
            supplierDongCheng.setLatWx(new BigDecimal(baseInfo.getWxLatitude().trim()));
            supplierDongCheng.setLonWx(new BigDecimal(baseInfo.getWxLongitude().trim()));
            String diannei = baseInfo.getDiannei();
            supplierDongCheng.setBreakfast(diannei.contains("有早餐"));
            supplierDongCheng.setBrandName(baseInfo.getBrandName());
            supplierDongCheng.setDescription(baseInfo.getDescription());
            supplierDongCheng.setCreateTime(now);
            supplierDongChengs.add(supplierDongCheng);
        }
        return supplierDongChengs;
    }

    private void getOneCityHotels(Integer cityId) {
        int pageIndex = 1;
        DongChengHotelRequest request = new DongChengHotelRequest();
        request.setCityId(cityId);
        request.setPageIndex(pageIndex);
        request.setPageSize(30);
        String data;
        int totalCount = 0;
        List<DongChengHotel> dongChengHotels = Lists.newArrayListWithCapacity(100);
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            DongChengGnResponse response = httpUtils.getDongChengHotel(request);
            data = response.getData();
            String msg = response.getMsg();
            log.info("cityId:{}返回错误消息:{}", cityId, msg);
            if (StringUtils.hasLength(msg)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    continue;
                }
            }
            totalCount = response.getTotalCount();
            if (totalCount == 0) {
                log.info("cityId{} 没有酒店", cityId);
                return;
            }
            dongChengHotels.addAll(JSON.parseArray(data, DongChengHotel.class));
        } while (totalCount > 0 && dongChengHotels.size() < totalCount);
        log.info("cityId{} 有{}家酒店", cityId, dongChengHotels.size());
        List<SupplierDongCheng> supplierDongChengs = parse(dongChengHotels);
       // supplierDongChengDao.saveBatch(supplierDongChengs);
    }
}
