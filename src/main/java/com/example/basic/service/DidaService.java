package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.DidaCountryDao;
import com.example.basic.dao.DidaHotelIdDao;
import com.example.basic.domain.DidaResponse;
import com.example.basic.entity.DidaCountry;
import com.example.basic.entity.DidaHotelId;
import com.example.basic.utils.HttpUtils;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author han
 * {@code @date} 2025/1/26
 */
@Slf4j
@Service
public class DidaService {

    @Resource
    private DidaCountryDao didaCountryDao;

    @Resource
    private DidaHotelIdDao didaHotelIdDao;

    @Resource
    private HttpUtils httpUtils;

    public void pullCountry() {
        //firstPull();
        String json = httpUtils.pullDidaCountryCn();
        DidaResponse didaResponse = JSON.parseObject(json, DidaResponse.class);
        String data = didaResponse.getData();
        JSONArray jsonArray = JSON.parseArray(data);
        List<DidaCountry> didaCountryList = Lists.newArrayListWithExpectedSize(jsonArray.size());
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            DidaCountry didaCountry = new DidaCountry();
            didaCountry.setCode(jsonObject.get("code").toString());
            didaCountry.setName(jsonObject.get("name").toString());
            didaCountryList.add(didaCountry);
        }
        for (DidaCountry didaCountry : didaCountryList) {
            didaCountryDao.updateName(didaCountry);
        }
    }

    private void firstPull() {
        String json = httpUtils.pullDidaCountryEn();
        DidaResponse didaResponse = JSON.parseObject(json, DidaResponse.class);
        String data = didaResponse.getData();
        JSONArray jsonArray = JSON.parseArray(data);
        List<DidaCountry> didaCountryList = Lists.newArrayListWithExpectedSize(jsonArray.size());
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            DidaCountry didaCountry = new DidaCountry();
            didaCountry.setCode(jsonObject.get("code").toString());
            didaCountry.setNameEn(jsonObject.get("name").toString());
            didaCountryList.add(didaCountry);
        }
        didaCountryDao.saveBatch(didaCountryList);
    }

    public void analyzeHotelInfo() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\dida\\HotelSummary.csv";
        List<String> lines = Files.readLines(new File(fileName), Charset.defaultCharset());
        List<DidaHotelId> insertList = Lists.newArrayListWithCapacity(lines.size());
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            DidaHotelId didaHotelId = new DidaHotelId();
            didaHotelId.setHotelId(Integer.valueOf(line.split("\\|")[0]));
            insertList.add(didaHotelId);
        }
        List<List<DidaHotelId>> partition = Lists.partition(insertList, 5000);
        partition.forEach(e->didaHotelIdDao.saveBatch(e));
    }

    public void pullHotelInfo() {
        List<Integer> hotelIds = didaHotelIdDao.selectAll();
    }
}
