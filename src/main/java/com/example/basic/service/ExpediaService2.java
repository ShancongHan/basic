package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.ExpediaCountryDao;
import com.example.basic.dao.ExpediaPropertyBasicDao;
import com.example.basic.dao.ExpediaRegionsDao;
import com.example.basic.domain.ExpediaResponse;
import com.example.basic.domain.Region;
import com.example.basic.domain.to.Coordinates;
import com.example.basic.entity.ExpediaCountry;
import com.example.basic.entity.ExpediaPropertyBasic;
import com.example.basic.entity.ExpediaRegions;
import com.example.basic.utils.HttpUtils;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author han
 * @date 2024/12/17
 */
@Slf4j
@Service
public class ExpediaService2 {

    @Resource
    private HttpUtils httpUtils;

    @Resource
    private ExpediaCountryDao expediaCountryDao;

    @Resource
    private ExpediaRegionsDao expediaRegionsDao;

    @Resource
    private ExpediaPropertyBasicDao expediaPropertyBasicDao;

    private static final Integer PRICE_CHUNK_SIZE = 100;

    private static final Executor executor3 = new ThreadPoolExecutor(PRICE_CHUNK_SIZE, PRICE_CHUNK_SIZE * 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(PRICE_CHUNK_SIZE * 10));

    public void pullRegions() throws Exception {
        List<ExpediaCountry> expediaCountries = expediaCountryDao.selectAll();
        List<String> skip = Lists.newArrayList("AD", "AE", "AF", "AG", "AI", "AL", "AM", "AO", "AR", "AS",
                "AT", "AU", "AX", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BL", "BM", "BN", "BO",
                "BQ", "BR", "BS", "BT", "BV", "BW", "BY", "BZ", "CA", "CC", "CD", "CF", "CG", "CH", "CI", "CK", "CL",
                "CM", "CN", "CO", "CR", "CU", "CV", "CW", "CX", "CY", "CZ", "DE", "DJ", "DK", "DM", "DO", "DZ", "EC",
                "EE", "EG", "ER", "ES", "ET", "FI", "FJ", "FK", "FM", "FO", "FR", "GA", "GB", "GD", "GE", "GF", "GG",
                "GH", "GI", "GL", "GM", "GN", "GP", "GQ", "GR", "GS", "GT", "GU", "GW", "GY", "HK", "HM", "HN", "HR",
                "HT", "HU", "ID", "IE", "IL", "IM", "IN", "IO", "IQ", "IR", "IS", "IT", "JE", "JM", "JP", "KG", "KH",
                "KM", "KN", "KP", "KY", "LC", "LI", "LK", "LR", "LT", "LU", "LY", "MA", "MC", "MD", "ME", "MF", "MG",
                "MH", "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS", "MT", "MU", "MV", "MW", "MX", "MY", "MZ",
                "NA", "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NZ", "OM", "PA", "PE", "PF", "PG",
                "PH", "PK", "PL", "PM", "PN", "PR", "PS", "PT", "PW", "PY", "QA", "RE", "RO", "RS", "RU", "RW", "SA",
                "SB", "SC", "SD", "SE", "SG", "SH", "SI", "SJ", "SK", "SL", "SM", "SN", "SO", "SR", "SS", "ST", "SV",
                "SX", "SY", "SZ", "TC", "TD", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TO", "TR", "TT", "TV",
                "TW", "TZ", "UA", "UG", "UM", "US", "UY", "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS",
                "YE", "YT", "ZA", "ZM", "ZW");
        boolean stop = false;
        for (ExpediaCountry expediaCountry : expediaCountries) {
            if (stop) break;
            String countryCode = expediaCountry.getCode();
            if (skip.contains(countryCode)) continue;
            List<ExpediaRegions> expediaRegions = Lists.newArrayListWithCapacity(128);
            int page = 0;
            Integer load;
            String nextPageUrl = null;
            do {
                StopWatch watch = new StopWatch();
                watch.start();
                if ("".equals(nextPageUrl)) break;
                ExpediaResponse response = httpUtils.pullRegionsV2(nextPageUrl, countryCode);
                watch.stop();
                log.info("获取第{}页内容, cost[s]: {},请求路径:{}", page, watch.getTotalTimeSeconds(), nextPageUrl);
                String body = response.getBody();
                try {
                    List<Region> regionList = JSON.parseArray(body, Region.class);
                    if (CollectionUtils.isNotEmpty(regionList)) {
                        for (Region region : regionList) {
                            ExpediaRegions expediaRegion = new ExpediaRegions();
                            expediaRegion.setRegionId(region.getId());
                            String type = region.getType();
                            expediaRegion.setType(type);
                            expediaRegion.setNameEn(region.getName());
                            expediaRegion.setFullNameEn(region.getName_full());
                            expediaRegion.setCountryCode(region.getCountry_code());
                            expediaRegion.setRealExist(true);
                            expediaRegions.add(expediaRegion);
                        }
                    }
                } catch (Exception e) {
                    log.info("转换异常{}", Throwables.getStackTraceAsString(e));
                    log.info("当前地址{}", nextPageUrl);
                    stop = true;
                    break;
                }
                load = response.getLoad();
                nextPageUrl = response.getNextPageUrl();
                if (CollectionUtils.isNotEmpty(expediaRegions)) {
                    expediaRegionsDao.saveBatch(expediaRegions);
                    expediaRegions.clear();
                }
                page++;
            } while (load > 0);
        }
    }

    public void finishRegions() {
        List<ExpediaRegions> expediaRegionList = expediaRegionsDao.selectRegions();
        int total = expediaRegionList.size();
        int start = 0;
        List<CompletableFuture<ExpediaRegions>> futures = Lists.newArrayListWithExpectedSize(PRICE_CHUNK_SIZE);
//        List<ExpediaRegions> updateList = Lists.newArrayListWithCapacity(futures.size());
        int chunkSize = PRICE_CHUNK_SIZE;
        for (int i = 0; i < total; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, total);
            for (ExpediaRegions expediaRegions : expediaRegionList.subList(i, endIndex)) {
                futures.add(CompletableFuture.supplyAsync(() -> httpUtils.pullRegion(expediaRegions.getRegionId(), expediaRegions.getId()), executor3));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            //List<ExpediaRegionsProperty> properties = Lists.newArrayList();
            for (CompletableFuture<ExpediaRegions> future : futures) {
                start++;
                try {
                    ExpediaRegions result = future.get(3, TimeUnit.SECONDS);
                    expediaRegionsDao.update(result);
                    /*List<String> propertyIds = result.getPropertyIds();
                    if (CollectionUtils.isNotEmpty(propertyIds)) {
                        for (String propertyId : propertyIds) {
                            ExpediaRegionsProperty property = new ExpediaRegionsProperty();
                            property.setRegionId(result.getRegionId());
                            property.setPropertyId(propertyId);
                            properties.add(property);
                        }
                    }*/
                } catch (TimeoutException e) {
                    log.info("查详情失败, 超时被跳过, 当前进度:{}/{}", start, total);
                } catch (Exception e) {
                    log.info("查详情失败, 当前进度:{}/{}", start, total);
                    log.error("错误信息:{}", Throwables.getStackTraceAsString(e));
                }
            }
            /*for (ExpediaRegions expediaRegions : updateList) {
                expediaRegionsDao.update(expediaRegions);
            }*/
            /*if (CollectionUtils.isNotEmpty(properties)) {
                expediaRegionsPropertyDao.saveBatch(properties);
            }*/
            futures.clear();
//            updateList.clear();
        }
    }

    public void analyzePropertyStaticFile() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\en-US.expediacollect.propertycatalog.jsonl";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        List<ExpediaPropertyBasic> expediaPropertyBasics = Lists.newArrayListWithCapacity(1000);
        for (String line : lines) {
            ExpediaPropertyBasic expediaPropertyBasic = parseTo(line);
            expediaPropertyBasics.add(expediaPropertyBasic);
            if (expediaPropertyBasics.size() == 1000) {
                expediaPropertyBasicDao.saveBatch(expediaPropertyBasics);
                expediaPropertyBasics.clear();
            }
        }
        if (expediaPropertyBasics.size() > 0) {
            expediaPropertyBasicDao.saveBatch(expediaPropertyBasics);
        }
    }

    private ExpediaPropertyBasic parseTo(String line) {
        JSONObject jsonObject = JSON.parseObject(line);
        ExpediaPropertyBasic expediaPropertyBasic = new ExpediaPropertyBasic();
        expediaPropertyBasic.setPropertyId((String) jsonObject.get("property_id"));
        expediaPropertyBasic.setNameEn((String) jsonObject.get("name"));
        JSONObject address = (JSONObject) jsonObject.get("address");
        if (address != null) {
            String line1 = (String) address.get("line_1");
            String line2 = (String) address.get("line_2");
            expediaPropertyBasic.setAddressEn(line1 + (StringUtils.hasLength(line2) ? line2 : ""));
            expediaPropertyBasic.setCountryCode((String) address.get("country_code"));
            expediaPropertyBasic.setStateProvinceCode((String) address.get("state_province_code"));
            expediaPropertyBasic.setStateProvinceName((String) address.get("state_province_name"));
            expediaPropertyBasic.setCity((String) address.get("city"));
            expediaPropertyBasic.setZipCode((String) address.get("postal_code"));
        }

        JSONObject ratings = (JSONObject) jsonObject.get("ratings");
        if (ratings != null) {
            JSONObject property = (JSONObject) ratings.get("property");
            if (property != null) {
                expediaPropertyBasic.setStarRating(new BigDecimal((String) property.get("rating")));
            }
        }

        JSONObject location = (JSONObject) jsonObject.get("location");
        if (location != null) {
            Object object = location.get("coordinates");
            if (StringUtils.hasLength(object.toString())) {
                Coordinates coordinates1 = JSON.parseObject(object.toString(), Coordinates.class);
                expediaPropertyBasic.setLongitude(coordinates1.getLongitude());
                expediaPropertyBasic.setLatitude(coordinates1.getLatitude());
                //String latitude = (String) coordinates.get("latitude");
                //expediaPropertyBasic.setLatitude((BigDecimal) coordinates.get("latitude"));
            }
        }

        expediaPropertyBasic.setTelephone((String) jsonObject.get("phone"));

        JSONObject category = (JSONObject) jsonObject.get("category");
        if (category != null) {
            expediaPropertyBasic.setCategoryId(Long.valueOf((String) category.get("id")));
            expediaPropertyBasic.setCategory((String) category.get("name"));
        }
        Integer rank = (Integer) jsonObject.get("rank");
        expediaPropertyBasic.setRank(Long.parseLong(String.valueOf(rank)));

        JSONObject businessModel = (JSONObject) jsonObject.get("business_model");
        if (businessModel != null) {
            expediaPropertyBasic.setExpediaCollect((Boolean) businessModel.get("expedia_collect"));
            expediaPropertyBasic.setPropertyCollect((Boolean) businessModel.get("property_collect"));
        }

        JSONObject statistics = (JSONObject) jsonObject.get("statistics");
        if (statistics != null) {
            StringBuilder ids = new StringBuilder();
            StringBuilder values = new StringBuilder();
            for (Map.Entry<String, Object> entry : statistics.entrySet()) {
                ids.append(entry.getKey()).append(",");
                values.append(((JSONObject) entry.getValue()).get("value")).append(",");
            }
            expediaPropertyBasic.setStatisticsId(ids.substring(0, ids.length() - 1));
            expediaPropertyBasic.setStatisticsValues(values.substring(0, values.length() - 1));
        }

        JSONObject chain = (JSONObject) jsonObject.get("chain");
        if (chain != null) {
            expediaPropertyBasic.setChainId((String) chain.get("id"));
        }

        JSONObject brand = (JSONObject) jsonObject.get("brand");
        if (brand != null) {
            expediaPropertyBasic.setBrandId((String) brand.get("id"));
        }
        expediaPropertyBasic.setSupplySource((String) jsonObject.get("supply_source"));

        JSONObject dates = (JSONObject) jsonObject.get("dates");
        if (dates != null) {
            expediaPropertyBasic.setAddedTime((String) dates.get("added"));
            expediaPropertyBasic.setUpdatedTime((String) dates.get("updated"));
        }
        return expediaPropertyBasic;
    }
}
