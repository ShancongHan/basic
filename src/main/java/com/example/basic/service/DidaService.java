package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.DidaCountryDao;
import com.example.basic.dao.DidaHotelIdDao;
import com.example.basic.domain.DidaResponse;
import com.example.basic.domain.dida.DidaHotelInfoResult;
import com.example.basic.domain.dida.HotelResponse;
import com.example.basic.entity.DidaCountry;
import com.example.basic.entity.DidaHotelId;
import com.example.basic.utils.HttpUtils;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.*;

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

    private static final Executor executor =
            new ThreadPoolExecutor(
                    20,
                    50,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(1000));

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
        int apiLimit = 10;
        int onceQueryHotelIdCount = 50;
        int onceBatchHotelIdSize = apiLimit * onceQueryHotelIdCount;
        int total = hotelIds.size();
        int totalProcess = total / onceBatchHotelIdSize;
        int process = 1;
        for (int i = 0; i < total; ) {
            // 一次查询id列表
            List<Integer> onceBatchIdList = hotelIds.subList(i, Math.min(total, i + onceBatchHotelIdSize));
            List<DidaHotelInfoResult> hotelInfoList = queryHotelInfo(onceBatchIdList, apiLimit, onceQueryHotelIdCount);
            StopWatch watch = new StopWatch();
            i += onceBatchHotelIdSize;
            process++;
            log.info("当前批次{}/{}, 耗时{},分别耗时为:{}", process, totalProcess, watch.getTotalTimeSeconds(), watch.prettyPrint());
        }
    }

    private List<DidaHotelInfoResult> queryHotelInfo(List<Integer> onceBatchIdList, int apiLimit, int onceQueryHotelIdCount) {
        List<DidaHotelInfoResult> hotelInfoList = Lists.newArrayListWithExpectedSize(apiLimit * onceQueryHotelIdCount);
        List<CompletableFuture<List<DidaHotelInfoResult>>> futures = Lists.newArrayListWithExpectedSize(apiLimit);
        int total = onceBatchIdList.size();
        for (int j = 0; j < apiLimit; j++) {
            int oneStart = j * onceQueryHotelIdCount;
            int oneEnd = Math.min(total, (j + 1) * onceQueryHotelIdCount);
            if (oneStart > total) break;
            List<Integer> onceQueryHotelIdList = onceBatchIdList.subList(oneStart, oneEnd);
            if (CollectionUtils.isEmpty(onceQueryHotelIdList)) break;
            futures.add(CompletableFuture.supplyAsync(() -> {
                String infoJson = httpUtils.pullDidaHotelInfoEn(onceQueryHotelIdList);
                boolean hasInfo = StringUtils.hasLength(infoJson);
                if (!hasInfo) return null;
                return handlerInfo(infoJson);
            }, executor));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        for (Future<List<DidaHotelInfoResult>> future : futures) {
            try {
                List<DidaHotelInfoResult> results = future.get(5, TimeUnit.SECONDS);
                if (CollectionUtils.isEmpty(results)) continue;
                hotelInfoList.addAll(results);
            } catch (Exception ignore) {
            }
        }
        return hotelInfoList;
    }

    private List<DidaHotelInfoResult> handlerInfo(String infoJson) {
        HotelResponse response = JSON.parseObject(infoJson, HotelResponse.class);
        return response.getData();
    }
}
