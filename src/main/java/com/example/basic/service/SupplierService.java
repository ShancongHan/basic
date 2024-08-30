package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.example.basic.dao.JdJdbDongchengDao;
import com.example.basic.dao.SupplierDongChengDao;
import com.example.basic.dao.ZhJdJdbMappingDao;
import com.example.basic.domain.*;
import com.example.basic.entity.JdJdbDongcheng;
import com.example.basic.entity.SupplierDongCheng;
import com.example.basic.entity.ZhJdJdbMapping;
import com.example.basic.utils.HttpUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    private JdJdbDongchengDao jdJdbDongchengDao;

    @Resource
    private ZhJdJdbMappingDao zhJdJdbMappingDao;

    @Resource
    private HttpUtils httpUtils;

    public void dcHotel() {
        DongChengGnResponse response = httpUtils.getDongChengCity();
        String data = response.getData();
        List<DongChengCity> dongChengCities = JSON.parseArray(data, DongChengCity.class);
        List<Integer> cityIds = dongChengCities.stream().map(DongChengCity::getCid).toList();
        log.info("东呈城市有{}个，分别是{}", dongChengCities.size(), cityIds);
        for (Integer cityId : cityIds) {
            getOneCityHotels(cityId);
        }
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
        List<DongChengHotel> dongChengHotels = Lists.newArrayListWithCapacity(200);
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            DongChengGnResponse response = httpUtils.getDongChengHotel(request);
            data = response.getData();
            String msg = response.getMsg();
            if (StringUtils.hasLength(msg)) {
                log.info("cityId:{}返回错误消息:{}", cityId, msg);
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
            request.setPageIndex(++pageIndex);
        } while (totalCount > 0 && dongChengHotels.size() < totalCount);
        log.info("cityId{} 有{}家酒店", cityId, dongChengHotels.size());
        List<SupplierDongCheng> supplierDongChengs = parse(dongChengHotels);
        supplierDongChengDao.saveBatch(supplierDongChengs);
    }

    public void dcHotelCheck() {
        /*DongChengGnResponse response = httpUtils.getDongChengCity();
        String data = response.getData();
        List<DongChengCity> dongChengCities = JSON.parseArray(data, DongChengCity.class);*/
        getOneCityHotels(22);
    }

    public void dcHotelMatch() {
        List<String> hotelIds = supplierDongChengDao.selectAllHotelIds();
        Map<String, String> map = Maps.newHashMapWithExpectedSize(2500);
        for (String hotelId : hotelIds) {
            map.put(hotelId, "1");
        }
        List<JdJdbDongcheng> oldHotels = jdJdbDongchengDao.selectMatchList();

        Map<String, Integer> map2 = Maps.newHashMapWithExpectedSize(2500);
        for (JdJdbDongcheng oldHotel : oldHotels) {
            String jdid = oldHotel.getJdid();
            if (!map.containsKey(jdid)) {
                map2.put(jdid, oldHotel.getYsbs());
            }
        }
       /* map2.entrySet().stream().filter((k,v)->{}).toList();
        log.info("找不到记录数：{}，分别是：{}", map2.size(), notFoundList);*/
        /*Map<String, Integer> oldHotelIdMap = oldHotels.stream().collect(Collectors.toMap(JdJdbDongcheng::getJdid, JdJdbDongcheng::getYsbs));
        List<String> toBeDeleteList = Lists.newArrayListWithCapacity(128);
        for (String hotelId : hotelIds) {
            oldHotelIdMap.remove(hotelId);
        }*/

        /*for (String hotelId : hotelIds) {
            if (!map.containsKey(hotelId)) {
                notFoundList.add(hotelId);
            }
        }*/
        //log.info("找不到记录数：{}，分别是：{}", notFoundList.size(), notFoundList);
    }

    public void xxx() {
        List<ZhJdJdbMapping> allList = zhJdJdbMappingDao.selectAll();
        List<ZhJdJdbMapping> dongchengs = allList.stream().filter(e -> e.getInterfacePlat().equals(2000050)).toList();
        List<ZhJdJdbMapping> huazhus = allList.stream().filter(e -> e.getInterfacePlat().equals(2000037)).toList();
        List<ZhJdJdbMapping> jinjiangs = allList.stream().filter(e -> e.getInterfacePlat().equals(2000044)).toList();
        List<ZhJdJdbMapping> meituans = allList.stream().filter(e -> e.getInterfacePlat().equals(2000023)).toList();
        List<ZhJdJdbMapping> elongs = allList.stream().filter(e -> e.getInterfacePlat().equals(2000004)).toList();
        Set<String> localIdMap = meituans.stream().map(ZhJdJdbMapping::getLocalId).collect(Collectors.toSet());
        Set<String> elongLocalIdMap = elongs.stream().map(ZhJdJdbMapping::getLocalId).collect(Collectors.toSet());
        List<String> dongchengPlatIds = dongchengs.stream().filter(e -> localIdMap.contains(e.getLocalId())).map(ZhJdJdbMapping::getPlatId).toList();
        List<String> huazhuPlatIds = huazhus.stream().filter(e -> localIdMap.contains(e.getLocalId())).map(ZhJdJdbMapping::getPlatId).toList();
        List<String> jinjiangPlatIds = jinjiangs.stream().filter(e -> localIdMap.contains(e.getLocalId())).map(ZhJdJdbMapping::getPlatId).toList();
        Set<String> meituanPlatIds = meituans.stream().filter(e -> elongLocalIdMap.contains(e.getLocalId())).map(ZhJdJdbMapping::getPlatId).collect(Collectors.toSet());
        String path = "C:\\wst_han\\打杂\\国内数据大整\\酒店数据\\第三期\\";
        String dongchengFile = path + "东呈.xlsx";
        List<HotelExport> dongchengExport = supplierDongChengDao.selectDongchengList(dongchengPlatIds);
        EasyExcel.write(dongchengFile, HotelExport.class).sheet("东呈与美团未映射酒店列表").doWrite(dongchengExport);

        String huazhuFile = path + "华住.xlsx";
        List<HotelExport> huazhuExport = supplierDongChengDao.selectHuazhuList(huazhuPlatIds);
        EasyExcel.write(huazhuFile, HotelExport.class).sheet("华住与美团未映射酒店列表").doWrite(huazhuExport);

        String jinjiangFile = path + "锦江.xlsx";
        List<HotelExport> jinjiangExport = supplierDongChengDao.selectJinjiangList(jinjiangPlatIds);
        EasyExcel.write(jinjiangFile, HotelExport.class).sheet("锦江与美团未映射酒店列表").doWrite(jinjiangExport);

        String meituanFile = path + "美团-艺龙.xlsx";
        List<HotelExport> meituanExport = supplierDongChengDao.selectMeituans();
        List<HotelExport> hotelExports = meituanExport.stream().filter(e -> !meituanPlatIds.contains(e.getHotelId())).toList();
        EasyExcel.write(meituanFile, HotelExport.class).sheet("美团与艺龙未映射酒店列表").doWrite(hotelExports);
    }
}
