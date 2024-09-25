package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.*;
import com.example.basic.domain.*;
import com.example.basic.domain.to.*;
import com.example.basic.entity.*;
import com.example.basic.entity.ExpediaContent;
import com.example.basic.helper.MappingScoreHelper2;
import com.example.basic.utils.HttpUtils;
import com.example.basic.utils.IOUtils;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/7/30
 */
@Slf4j
@Service
public class ExpediaService {

    @Resource
    private ExpediaContinentDao expediaContinentDao;

    @Resource
    private ExpediaCountryDao expediaCountryDao;

    @Resource
    private ExpediaRegionDao expediaRegionDao;
    @Resource
    private ExpediaRegionsDao expediaRegionsDao;
    @Resource
    private ExpediaRegionsPropertyDao expediaRegionsPropertyDao;

    @Resource
    private ExpediaContentDao expediaContentDao;

    @Resource
    private ExpediaAmenitiesPropertyDao expediaAmenitiesPropertyDao;
    @Resource
    private ExpediaAmenitiesRatesDao expediaAmenitiesRatesDao;
    @Resource
    private ExpediaAmenitiesRoomsDao expediaAmenitiesRoomsDao;
    @Resource
    private ExpediaAttributesGeneralDao expediaAttributesGeneralDao;
    @Resource
    private ExpediaAttributesPetsDao expediaAttributesPetsDao;
    @Resource
    private ExpediaCategoriesDao expediaCategoriesDao;
    @Resource
    private ExpediaImagesDao expediaImagesDao;
    @Resource
    private ExpediaRoomViewsDao expediaRoomViewsDao;
    @Resource
    private ExpediaSpokenLanguagesDao expediaSpokenLanguagesDao;
    @Resource
    private ExpediaStatisticsDao expediaStatisticsDao;
    @Resource
    private ExpediaThemesDao expediaThemesDao;
    @Resource
    private ExpediaChainBrandsDao expediaChainBrandsDao;
    @Resource
    private ExpediaPropertyBasicDao expediaPropertyBasicDao;
    @Resource
    private JdJdbDaolvDao jdJdbDaolvDao;
    @Resource
    private ExpediaDaolvMatchLabCountDao expediaDaolvMatchLabCountDao;
    @Resource
    private ExpediaDaolvMatchLabDao expediaDaolvMatchLabDao;

    @Resource
    private ZhJdJdbGjMappingDao zhJdJdbGjMappingDao;

    @Resource
    private HttpUtils httpUtils;

    @Resource
    private ExpediaContentBasicDao expediaContentBasicDao;

    private static final Integer CORE_POOL_SIZE = 200;
    private static final Integer MAXIMUM_POOL_SIZE = 250;

    private static final Integer PRICE_CHUNK_SIZE = 100;

    private static final Executor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(3000));

    private static final Executor executor2 = new ThreadPoolExecutor(20, 50,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100));

    private static final Executor executor3 = new ThreadPoolExecutor(PRICE_CHUNK_SIZE, PRICE_CHUNK_SIZE * 2,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(PRICE_CHUNK_SIZE * 10));

    public void continent() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\对接\\expedia大洲划分.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        EasyExcel.read(inputStream, ExpediaContinent.class, new PageReadListener<ExpediaContinent>(dataList -> {
            expediaContinentDao.saveBatch(dataList);
        }, 1000)).headRowNumber(1).sheet().doRead();
    }

    public void country() throws FileNotFoundException {
        String fileName = "C:\\wst_han\\打杂\\expedia\\对接\\expedia国家划分.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        EasyExcel.read(inputStream, ExpediaCountry.class, new PageReadListener<ExpediaCountry>(dataList -> {
            expediaCountryDao.saveBatch(dataList);
        }, 1000)).headRowNumber(1).sheet().doRead();
    }

    public void pullRegions() throws IOException {
        List<ExpediaRegion> expediaRegions = Lists.newArrayListWithCapacity(128);
        int page = 7643;
        Integer load;
        String nextPageUrl = "https://test.ean.com/v3/regions?token=Q11RF1Vda1JREAQQCQ4DVwhUBFsCD1IMVFEMCgEEWx4DSRZaR1wXXgZUC1AZVgsbCVdhVFEQUCAED0cHc1QHHAAFBwYBBQgFBTgSDFpXDUNcVEYIEVEnFwtXVFNBWQ9UQ0EEARJUd0VUVQoGQVlVBAQcXUhyeR8mWwZQXUcTXlkKAQAGAA0LUwYN";
        do {
            StopWatch watch = new StopWatch();
            watch.start();
            if ("".equals(nextPageUrl)) break;
            ExpediaResponse response = httpUtils.pullRegions(nextPageUrl);
            watch.stop();
            log.info("获取第{}页内容, cost[s]: {},请求路径:{}", page, watch.getTotalTimeSeconds(), nextPageUrl);
            String body = response.getBody();
            try {
                transferBody(expediaRegions, body);
            } catch (Exception e) {
                Throwables.getStackTraceAsString(e);
            }
            load = response.getLoad();
            nextPageUrl = response.getNextPageUrl();
            expediaRegionDao.saveBatch(expediaRegions);
            expediaRegions.clear();
            page++;
        } while (load > 0);
    }

    private void transferBody(List<ExpediaRegion> expediaRegions, String body) {
        List<Region> regionList = JSON.parseArray(body, Region.class);
        if (CollectionUtils.isNotEmpty(regionList)) {
            //转换成expediaRegions
            for (Region region : regionList) {
                ExpediaRegion expediaRegion = new ExpediaRegion();
                expediaRegion.setRegionId(region.getId());
                String type = region.getType();
                expediaRegion.setType(type);
                expediaRegion.setNameEn(region.getName());
                expediaRegion.setNameFullEn(region.getName_full());
                expediaRegion.setCountryCode(region.getCountry_code());
                expediaRegion.setCountrySubdivisionCode(region.getCountry_subdivision_code());
                List<Ancestors> ancestors = region.getAncestors();
                if (CollectionUtils.isEmpty(ancestors)) {
                    expediaRegion.setParentId("0");
                    expediaRegion.setParentPath(null);
                } else {
                    // type == country
                    if (ancestors.size() == 1) {
                        expediaRegion.setParentId(ancestors.get(0).getId());
                        expediaRegion.setParentPath("/0/" + ancestors.get(0).getId() + "/");
                    }
                    // type == province_state
                    if ("province_state".equals(type)) {
                        List<Ancestors> list = ancestors.stream().filter(e -> "country".equals(e.getType())).toList();
                        if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
                            expediaRegion.setParentId(list.get(0).getId());
                        }
                    }
                    if ("multi_city_vicinity".equals(type)) {
                        List<Ancestors> list = ancestors.stream().filter(e -> "province_state".equals(e.getType())).toList();
                        if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
                            expediaRegion.setParentId(list.get(0).getId());
                        }
                    }
                    if ("neighborhood".equals(type)) {
                        List<Ancestors> list = ancestors.stream().filter(e -> "city".equals(e.getType())).toList();
                        if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
                            expediaRegion.setParentId(list.get(0).getId());
                        }
                    }

                }
                expediaRegion.setCenterLatitude(BigDecimal.valueOf(region.getCoordinates().getCenter_latitude()));
                expediaRegion.setCenterLongitude(BigDecimal.valueOf(region.getCoordinates().getCenter_longitude()));
                if (CollectionUtils.isNotEmpty(region.getCategories())) {
                    expediaRegion.setCategories(region.getCategories().toString());
                }
                if (CollectionUtils.isNotEmpty(region.getTags())) {
                    expediaRegion.setTags(region.getTags().toString());
                }
                expediaRegion.setAncestors(JSON.toJSONString(region.getAncestors()));
                expediaRegion.setDescendants(JSON.toJSONString(region.getDescendants()));
                expediaRegions.add(expediaRegion);
            }
        }
    }

    public void pullContent(Integer page, String url) throws Exception {
        /*List<ExpediaAmenitiesProperty> expediaAmenitiesProperties = expediaAmenitiesPropertyDao.selectAll();
        Map<Long, ExpediaAmenitiesProperty> amenitiesPropertyMap = expediaAmenitiesProperties.stream().collect(Collectors.toMap(ExpediaAmenitiesProperty::getId, Function.identity()));
        List<ExpediaAmenitiesRates> expediaAmenitiesRates = expediaAmenitiesRatesDao.selectAll();
        Map<Long, ExpediaAmenitiesRates> amenitiesRatesMap = expediaAmenitiesRates.stream().collect(Collectors.toMap(ExpediaAmenitiesRates::getId, Function.identity()));
        List<ExpediaAmenitiesRooms> expediaAmenitiesRooms = expediaAmenitiesRoomsDao.selectAll();
        Map<Long, ExpediaAmenitiesRooms> amenitiesRoomsMap = expediaAmenitiesRooms.stream().collect(Collectors.toMap(ExpediaAmenitiesRooms::getId, Function.identity()));
        List<ExpediaAttributesGeneral> expediaAttributesGenerals = expediaAttributesGeneralDao.selectAll();
        Map<Long, ExpediaAttributesGeneral> attributesGeneralMap = expediaAttributesGenerals.stream().collect(Collectors.toMap(ExpediaAttributesGeneral::getId, Function.identity()));
        List<ExpediaAttributesPets> expediaAttributesPets = expediaAttributesPetsDao.selectAll();
        Map<Long, ExpediaAttributesPets> attributesPetsMap = expediaAttributesPets.stream().collect(Collectors.toMap(ExpediaAttributesPets::getId, Function.identity()));
        List<ExpediaCategories> expediaCategories = expediaCategoriesDao.selectAll();
        Map<Long, ExpediaCategories> categoriesMap = expediaCategories.stream().collect(Collectors.toMap(ExpediaCategories::getId, Function.identity()));
        List<ExpediaImages> expediaImages = expediaImagesDao.selectAll();
        Map<Long, ExpediaImages> imagesMap = expediaImages.stream().collect(Collectors.toMap(ExpediaImages::getId, Function.identity()));
        List<ExpediaRoomViews> expediaRoomViews = expediaRoomViewsDao.selectAll();
        Map<Long, ExpediaRoomViews> roomViewsMap = expediaRoomViews.stream().collect(Collectors.toMap(ExpediaRoomViews::getId, Function.identity()));
        List<ExpediaSpokenLanguages> expediaSpokenLanguages = expediaSpokenLanguagesDao.selectAll();
        Map<String, ExpediaSpokenLanguages> spokenLanguagesMap = expediaSpokenLanguages.stream().collect(Collectors.toMap(ExpediaSpokenLanguages::getId, Function.identity()));
        List<ExpediaStatistics> expediaStatistics = expediaStatisticsDao.selectAll();
        Map<Long, ExpediaStatistics> statisticsMap = expediaStatistics.stream().collect(Collectors.toMap(ExpediaStatistics::getId, Function.identity()));
        List<ExpediaThemes> expediaThemes = expediaThemesDao.selectAll();
        Map<Long, ExpediaThemes> themesMap = expediaThemes.stream().collect(Collectors.toMap(ExpediaThemes::getId, Function.identity()));*/

        List<ExpediaContent> expediaContents = Lists.newArrayListWithCapacity(256);
        page = page == null ? 0 : page;
        Integer load;
        String nextPageUrl = url;
        // 629479
        // 632249
        do {
            StopWatch watch = new StopWatch();
            watch.start();
            if ("".equals(nextPageUrl)) break;
            ExpediaResponse response = httpUtils.pullContent(nextPageUrl);
            watch.stop();

            String body = response.getBody();
            //JSONObject jsonObject = response.getJsonObject();
            load = response.getLoad();
            log.info("获取第{}页内容, cost[s]: {},当前请求路径:{}", page, watch.getTotalTimeSeconds(), nextPageUrl);
            nextPageUrl = response.getNextPageUrl();
            log.info("下一页请求路径:{}", nextPageUrl);
            try {
                transferBody2(expediaContents, body);
            } catch (Exception e) {
                log.error("转换对象异常:{}", Throwables.getStackTraceAsString(e));
                throw new Exception("当前第" + page + "页，请求路径：" + nextPageUrl + "转换数据出错");
            }
            expediaContentDao.saveBatch(expediaContents);
            expediaContents.clear();
            page++;
        } while (load > 0);
    }

    private void transferBody2(List<ExpediaContent> expediaContents, String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object value = entry.getValue();
            Content content = JSON.parseObject(value.toString(), Content.class);
            ExpediaContent expediaContent = new ExpediaContent();
            expediaContent.setHotelId(content.getProperty_id());
            expediaContent.setName(content.getName());
            Address address = content.getAddress();
            expediaContent.setAddress(address.getLine_1() + (StringUtils.hasLength(address.getLine_2()) ? address.getLine_2() : ""));
            expediaContent.setCountryCode(address.getCountry_code());
            expediaContent.setStateProvinceCode(address.getState_province_code());
            expediaContent.setStateProvinceName(address.getState_province_name());
            expediaContent.setCity(address.getCity());
            expediaContent.setZipCode(address.getPostal_code());
            Ratings ratings = content.getRatings();
            if (ratings != null && ratings.getProperty() != null && ratings.getProperty().getRating() != null) {
                expediaContent.setStarRating(new BigDecimal(ratings.getProperty().getRating()));
            }
            Location location = content.getLocation();
            if (location != null && location.getCoordinates() != null) {
                expediaContent.setLongitude(location.getCoordinates().getLongitude());
                expediaContent.setLatitude(location.getCoordinates().getLatitude());
            }
            expediaContent.setTelephone(content.getPhone());
            expediaContent.setFax(content.getFax());
            String category = content.getCategory();
            expediaContent.setCategoryId(Long.valueOf(JSON.parseObject(category).get("id").toString()));
            expediaContent.setCategory(JSON.parseObject(category).get("name").toString());
            expediaContent.setRank((long) content.getRank());
            BusinessModel businessModel = content.getBusiness_model();
            if (businessModel != null) {
                expediaContent.setExpediaCollect(businessModel.isExpedia_collect());
                expediaContent.setPropertyCollect(businessModel.isProperty_collect());
            }
            HotelDates dates = content.getDates();
            if (dates != null) {
                expediaContent.setAddedTime(dates.getAdded());
                expediaContent.setUpdatedTime(dates.getUpdated());
            }
            String themes = content.getThemes();
            if (StringUtils.hasLength(themes)) {
                expediaContent.setThemes(String.join(",", JSON.parseObject(themes).keySet()));
            }
            expediaContent.setTaxId(content.getTax_id());
            String chain = content.getChain();
            if (StringUtils.hasLength(chain)) {
                expediaContent.setChain(JSON.parseObject(chain).values().stream().map(Object::toString).collect(Collectors.joining(",")));
            }
            String brand = content.getBrand();
            if (StringUtils.hasLength(brand)) {
                expediaContent.setBrand(JSON.parseObject(brand).values().stream().map(Object::toString).collect(Collectors.joining(",")));
            }
            String spokenLanguages = content.getSpoken_languages();
            if (StringUtils.hasLength(spokenLanguages)) {
                expediaContent.setSpokenLanguages(String.join(",", JSON.parseObject(spokenLanguages).keySet()));
            }
            List<Images> images = content.getImages();
            if (CollectionUtils.isNotEmpty(images)) {
                Optional<Images> first = images.stream().filter(Images::isHero_image).findFirst();
                first.ifPresent(e -> {
                    expediaContent.setHeroImageMin(((JSONObject) JSON.parseObject(e.getLinks()).get("70px")).get("href").toString());
                    expediaContent.setHeroImageMiddle(((JSONObject) JSON.parseObject(e.getLinks()).get("350px")).get("href").toString());
                });
            }
            expediaContent.setCreateTime(new Date());
            expediaContents.add(expediaContent);
        }
    }

    public void analyzeStaticFile() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\Content_Reference_Lists_3.json";
        String fileContent = IOUtils.inputStreamToString(new FileInputStream(fileName));
        JSONObject jsonObject = JSON.parseObject(fileContent);
        JSONObject value = (JSONObject) jsonObject.get("spoken_languages");
        List<ExpediaSpokenLanguages> insertList = Lists.newArrayList();
        Date now = new Date();
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            ExpediaSpokenLanguages item = JSON.parseObject(entry.getValue().toString(), ExpediaSpokenLanguages.class);
            item.setCreateTime(now);
            insertList.add(item);
        }
        expediaSpokenLanguagesDao.saveBatch(insertList);
    }

    public void finishRegions(Integer page, String url) throws Exception {
        List<ExpediaRegion> expediaRegions = Lists.newArrayListWithCapacity(128);
        List<ExpediaRegion> notFoundExpediaRegions = Lists.newArrayList();
        page = page == null ? 0 : page;
        Integer load;
        String nextPageUrl = url;
        do {
            StopWatch watch = new StopWatch();
            watch.start();
            if ("".equals(nextPageUrl)) break;
            ExpediaResponse response = httpUtils.pullRegionsEn(nextPageUrl);
            watch.stop();
            log.info("获取第{}页内容, cost[s]: {},请求路径:{}", page, watch.getTotalTimeSeconds(), nextPageUrl);
            String body = response.getBody();
            load = response.getLoad();
            nextPageUrl = response.getNextPageUrl();
            log.info("下一页请求路径:{}", nextPageUrl);
            try {
                addBody(expediaRegions, body);
            } catch (Exception e) {
                Throwables.getStackTraceAsString(e);
            }
            update(expediaRegions, notFoundExpediaRegions);
            expediaRegions.clear();
            log.info("第{}页中存在这些ids:{}没有数据", page, notFoundExpediaRegions.stream().map(ExpediaRegion::getId).toList());
            notFoundExpediaRegions.clear();
            page++;
        } while (load > 0);
    }

    private void update(List<ExpediaRegion> expediaRegions, List<ExpediaRegion> notFoundExpediaRegions) {
        for (ExpediaRegion expediaRegion : expediaRegions) {
            int update = expediaRegionDao.update(expediaRegion);
            if (update != 1) {
                notFoundExpediaRegions.add(expediaRegion);
            }
        }

    }

    private void addBody(List<ExpediaRegion> expediaRegions, String body) {
        List<Region> regionList = JSON.parseArray(body, Region.class);
        if (CollectionUtils.isNotEmpty(regionList)) {
            //转换成expediaRegions
            for (Region region : regionList) {
                ExpediaRegion expediaRegion = new ExpediaRegion();
                expediaRegion.setRegionId(region.getId());
                expediaRegion.setNameEn(region.getName());
                expediaRegion.setNameFullEn(region.getName_full());
                expediaRegions.add(expediaRegion);
            }
        }
    }

    public void pullChain() throws Exception {
        List<ExpediaChainBrands> expediaChainBrandsList = Lists.newArrayList();
        String response = httpUtils.pullChain();
        try {
            transferBody3(expediaChainBrandsList, response);
        } catch (Exception e) {
            log.error("解析发生异常:{}", Throwables.getStackTraceAsString(e));
        }
        int start = 0;
        for (int j = 0; j < expediaChainBrandsList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<ExpediaChainBrands> list = expediaChainBrandsList.subList(start, j);
                expediaChainBrandsDao.saveBatch(list);
                start = j;
            }
        }
        List<ExpediaChainBrands> list = expediaChainBrandsList.subList(start, expediaChainBrandsList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            expediaChainBrandsDao.saveBatch(list);
        }
        //expediaChainBrandsDao.saveBatch(expediaChainBrandsList);
    }

    private void transferBody3(List<ExpediaChainBrands> expediaChainBrandsList, String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            ExpediaChainBrands expediaChainBrands = new ExpediaChainBrands();
            JSONObject value = (JSONObject) entry.getValue();
            expediaChainBrands.setChainId((String) value.get("id"));
            expediaChainBrands.setName((String) value.get("name"));
            JSONObject object = (JSONObject) value.get("brands");

            if (object != null) {
                StringBuilder ids = new StringBuilder();
                StringBuilder names = new StringBuilder();
                for (Map.Entry<String, Object> entry1 : object.entrySet()) {
                    JSONObject value1 = (JSONObject) entry1.getValue();
                    ids.append(value1.get("id")).append(",");
                    names.append(value1.get("name")).append(",");
                }
                expediaChainBrands.setBrandsId(ids.substring(0, ids.length() - 1));
                expediaChainBrands.setBrandsName(names.substring(0, names.length() - 1));
            }
            expediaChainBrandsList.add(expediaChainBrands);
        }
    }

    public void matchRegion() {

    }

    public void analyzePropertyStaticFile() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\对接\\zh-CN.expediacollect.propertycatalog.jsonl";
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
        expediaPropertyBasic.setName((String) jsonObject.get("name"));
        JSONObject address = (JSONObject) jsonObject.get("address");
        if (address != null) {
            String line1 = (String) address.get("line_1");
            String line2 = (String) address.get("line_2");
            expediaPropertyBasic.setAddress(line1 + (StringUtils.hasLength(line2) ? line2 : ""));
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

    public void analyzePropertyStaticFile2() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\对接\\en-US.expediacollect.propertycatalog.jsonl";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        List<ExpediaPropertyBasic> expediaPropertyBasics = Lists.newArrayListWithCapacity(3000);
        for (String line : lines) {
            ExpediaPropertyBasic expediaPropertyBasic = parseTo2(line);
            int update = expediaPropertyBasicDao.update(expediaPropertyBasic);
            if (update != 1) {
                expediaPropertyBasics.add(expediaPropertyBasic);
            }
        }
        if (expediaPropertyBasics.size() > 0) {
            expediaPropertyBasicDao.saveBatch(expediaPropertyBasics);
        }
    }

    private ExpediaPropertyBasic parseTo2(String line) {
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

    public void mapping() throws Exception {
        List<String> countries = Lists.newArrayList("ID", "IN", "GB", "IT", "US");
        //已经匹配完成的国家，跳过去
        Set<String> alreadyMatch = expediaDaolvMatchLabCountDao.alreadyMatchCountry();
        for (String countryCode : countries) {
            if (alreadyMatch.contains(countryCode)) continue;

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ExpediaDaolvMatchLabCount matchLabCount = new ExpediaDaolvMatchLabCount();

            List<ExpediaPropertyBasic> expediaPropertyBasics = expediaPropertyBasicDao.selectListByCountry(countryCode);
            List<JdJdbDaolv> jdJdbDaolvList = jdJdbDaolvDao.selectListByCountry(countryCode);
            if (CollectionUtils.isEmpty(jdJdbDaolvList)) continue;

            matchLabCount.setCountryCode(countryCode);
            matchLabCount.setExpediaTotal(expediaPropertyBasics.size());
            matchLabCount.setDaolvTotal(jdJdbDaolvList.size());

            Map<String, List<ExpediaPropertyBasic>> expediaDataMap = expediaPropertyBasics.stream().filter(e -> StringUtils.hasLength(e.getNameEn())).collect(Collectors.groupingBy(ExpediaPropertyBasic::getNameEn));
            Map<String, List<JdJdbDaolv>> daolvDataMap = jdJdbDaolvList.stream().collect(Collectors.groupingBy(JdJdbDaolv::getName));

            List<ExpediaDaolvMatchLab> uniqueList = Lists.newArrayListWithCapacity(expediaPropertyBasics.size() * 2);
            List<ExpediaDaolvMatchLab> multiList = Lists.newArrayListWithCapacity(expediaPropertyBasics.size() * 2);
            Set<String> zeroScores = Sets.newHashSetWithExpectedSize(expediaPropertyBasics.size());
            int index = 1;
            int max = expediaDataMap.entrySet().size();
            for (Map.Entry<String, List<ExpediaPropertyBasic>> entry : expediaDataMap.entrySet()) {
                String hotelName = entry.getKey();
                StopWatch watch = new StopWatch();
                watch.start("酒店: " + countryCode + "-" + hotelName);
                List<ExpediaPropertyBasic> expediaPropertyBasicList = entry.getValue();
                List<JdJdbDaolv> findDaolvHotels = daolvDataMap.get(hotelName);
                int size = jdJdbDaolvList.size();
                // 有同名酒店，计算得分
                if (CollectionUtils.isNotEmpty(findDaolvHotels)) {
                    size = findDaolvHotels.size();
                    findMatch(uniqueList, expediaPropertyBasicList, findDaolvHotels, zeroScores, multiList);
                } else {
                    // 无同名酒店，全部匹配计算得分绝对值
                    findMatch(uniqueList, expediaPropertyBasicList, jdJdbDaolvList, zeroScores, multiList);
                }
                watch.stop();
                System.out.println("[" + index + "/" + max + "]酒店: " + countryCode + "-" + hotelName +
                        "查询范围为:" + size + ";耗时" + (int) watch.getTotalTimeMillis());
                index++;
            }
            stopWatch.stop();
            matchLabCount.setTime((int) stopWatch.getTotalTimeSeconds());
            matchLabCount.setExpediaNotScoreCount(zeroScores.size());
            matchLabCount.setExpediaUniqueScoreCount((int) uniqueList.stream().map(ExpediaDaolvMatchLab::getExpediaHotelId).distinct().count());
            matchLabCount.setExpediaMultiScoreCount((int) multiList.stream().map(ExpediaDaolvMatchLab::getExpediaHotelId).distinct().count());
            matchLabCount.setExpediaScoreCount(matchLabCount.getExpediaMultiScoreCount() + matchLabCount.getExpediaUniqueScoreCount());
            expediaDaolvMatchLabCountDao.insert(matchLabCount);
            saveBatch2(uniqueList);
            saveBatch3(multiList);
        }
    }

    private void findMatch(List<ExpediaDaolvMatchLab> uniqueList, List<ExpediaPropertyBasic> expediaPropertyBasicList,
                           List<JdJdbDaolv> daolvHotels, Set<String> zeroScores,
                           List<ExpediaDaolvMatchLab> multiList) throws Exception {
        for (ExpediaPropertyBasic expediaPropertyBasic : expediaPropertyBasicList) {
            List<CompletableFuture<CalculateResult>> futures = Lists.newArrayListWithCapacity(256);
            int size = daolvHotels.size();
            if (size < CORE_POOL_SIZE) {
                futures.add(executeOnceTask(expediaPropertyBasic, daolvHotels));
            } else {
                int chunkSize = (size / CORE_POOL_SIZE) + 1;
                for (int i = 0; i < daolvHotels.size(); i += chunkSize) {
                    final int endIndex = Math.min(i + chunkSize, daolvHotels.size());
                    futures.add(executeOnceTask(expediaPropertyBasic, daolvHotels.subList(i, endIndex)));
                }
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            Map<Integer, List<JdJdbDaolv>> scoreMap = Maps.newHashMapWithExpectedSize(265);
            for (CompletableFuture<CalculateResult> future : futures) {
                CalculateResult result = future.get();
                // 如果0分，跳过此部分评分
                if (result.getZero()) {
                    continue;
                }
                for (Map.Entry<Integer, List<JdJdbDaolv>> reslutEntry : result.getScoreMap().entrySet()) {
                    Integer score = reslutEntry.getKey();
                    List<JdJdbDaolv> sameScoreHotels = reslutEntry.getValue();
                    if (scoreMap.containsKey(score)) {
                        scoreMap.get(score).addAll(sameScoreHotels);
                    } else {
                        scoreMap.put(score, sameScoreHotels);
                    }
                }
            }
            // 一个webbeds酒店，最终没有分数，计为0分
            if (scoreMap.isEmpty()) {
                zeroScores.add(expediaPropertyBasic.getPropertyId());
                return;
            }
            // 一次结束后，能得到同名的最大得分的记录，唯一一条存入uniqueList，存在同分的多条存入multiList
            scoreMap.entrySet().stream()
                    .max(Comparator.comparingInt(entry1 -> Math.abs(entry1.getKey())))
                    .ifPresent(max -> createMatchResult(expediaPropertyBasic, max, uniqueList, multiList));
        }
    }

    private CompletableFuture<CalculateResult> executeOnceTask(ExpediaPropertyBasic expediaPropertyBasic, List<JdJdbDaolv> daolvHotels) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Integer, List<JdJdbDaolv>> scoreMap = Maps.newConcurrentMap();
            for (JdJdbDaolv daolvHotel : daolvHotels) {
                Integer score = MappingScoreHelper2.calculateScore(expediaPropertyBasic, daolvHotel);
                if (score == 0) continue;
                if (score == 16 || score == -16) {
                    CalculateResult result = new CalculateResult();
                    Map<Integer, List<JdJdbDaolv>> fullScoreMap = Maps.newConcurrentMap();
                    fullScoreMap.put(score, Lists.newArrayList(daolvHotel));
                    result.setScoreMap(fullScoreMap);
                    result.setZero(Boolean.FALSE);
                    return result;
                }
                if (scoreMap.containsKey(score)) {
                    scoreMap.get(score).add(daolvHotel);
                } else {
                    scoreMap.put(score, Lists.newArrayList(daolvHotel));
                }
            }
            CalculateResult result = new CalculateResult();
            result.setScoreMap(scoreMap);
            if (scoreMap.isEmpty()) {
                result.setZero(Boolean.TRUE);
            } else {
                result.setZero(Boolean.FALSE);
            }
            return result;
        }, executor);
    }

    private void createMatchResult(ExpediaPropertyBasic expediaPropertyBasic, Map.Entry<Integer, List<JdJdbDaolv>> max,
                                   List<ExpediaDaolvMatchLab> uniqueList, List<ExpediaDaolvMatchLab> multiList) {
        Integer score = max.getKey();
        List<JdJdbDaolv> jdJdbDaolvs = max.getValue();
        if (jdJdbDaolvs.size() > 1) {
            // -1分数过多，直接丢弃
            if (score.equals(-1)) {
                return;
            }
            for (JdJdbDaolv jdJdbDaolv : jdJdbDaolvs) {
                multiList.add(createOneMatchLab(expediaPropertyBasic, jdJdbDaolv, score, true));
            }
            return;
        }
        uniqueList.add(createOneMatchLab(expediaPropertyBasic, jdJdbDaolvs.get(0), score, false));
    }

    private ExpediaDaolvMatchLab createOneMatchLab(ExpediaPropertyBasic expediaPropertyBasic, JdJdbDaolv jdJdbDaolv,
                                                   Integer score, boolean multiMatch) {
        Double meter = MappingScoreHelper2.calculateMeter(expediaPropertyBasic, jdJdbDaolv);
        return null;
        /*return ExpediaDaolvMatchLab.builder()
                .expediaHotelId(expediaPropertyBasic.getPropertyId())
                .expediaHotelName(expediaPropertyBasic.getNameEn())
                .expediaCountry(expediaPropertyBasic.getCountryCode())
                .expediaLatitude(expediaPropertyBasic.getLatitude().toString())
                .expediaLongitude(expediaPropertyBasic.getLongitude().toString())
                .expediaAddress(expediaPropertyBasic.getAddress())
                .expediaTel(expediaPropertyBasic.getTelephone())
                .expediaCategory(expediaPropertyBasic.getCategory())
                .daolvHotelId(jdJdbDaolv.getId())
                .daolvHotelName(jdJdbDaolv.getName())
                .daolvLatitude(jdJdbDaolv.getLatitude() == null ? null : jdJdbDaolv.getLatitude().toString())
                .daolvLongitude(jdJdbDaolv.getLongitude() == null ? null : jdJdbDaolv.getLongitude().toString())
                .daolvCountry(jdJdbDaolv.getCountryCode())
                .daolvAddress(jdJdbDaolv.getAddress())
                .daolvTel(jdJdbDaolv.getTelephone())
                .diffMeter(meter == null ? null : BigDecimal.valueOf(meter))
                .nameMatch(Math.abs(score) >= 10 ? 1 : 0)
                .addressMatch(Math.abs(score) == 5 || Math.abs(score) == 6 || Math.abs(score) == 15 || Math.abs(score) == 16 ? 1 : 0)
                .telMatch(Math.abs(score) == 1 || Math.abs(score) == 6 || Math.abs(score) == 11 || Math.abs(score) == 16 ? 1 : 0)
                .score(score)
                .multiMatch(multiMatch ? 1 : 0).build();*/
    }

    private void saveBatch2(List<ExpediaDaolvMatchLab> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<ExpediaDaolvMatchLab> list = insertList.subList(start, j);
                expediaDaolvMatchLabDao.saveBatch(list);
                start = j;
            }
        }
        List<ExpediaDaolvMatchLab> list = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            expediaDaolvMatchLabDao.saveBatch(list);
        }
    }

    private void saveBatch3(List<ExpediaDaolvMatchLab> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<ExpediaDaolvMatchLab> list = insertList.subList(start, j);
                expediaDaolvMatchLabDao.saveBatch2(list);
                start = j;
            }
        }
        List<ExpediaDaolvMatchLab> list = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            expediaDaolvMatchLabDao.saveBatch2(list);
        }
    }

    public void finishProperty() throws Exception {
        List<String> propertyIds = expediaPropertyBasicDao.selectNeedUpdate();
        int max = Math.max(250, propertyIds.size());
        for (int i = 0; i < max; ) {
            int start = i;
            int end = Math.min(i + 250, max);
            List<String> queryList = propertyIds.subList(start, end);
            String body = httpUtils.pullPropertyListByIds(queryList);
            i += 250;
            JSONObject jsonObject = JSON.parseObject(body);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                System.out.println(entry.getKey());
                /*ExpediaPropertyBasic expediaPropertyBasic = new ExpediaPropertyBasic();
                String key = entry.getKey();
                expediaPropertyBasic.setPropertyId(key);
                JSONObject value = (JSONObject) entry.getValue();
                //expediaPropertyBasic.setNameEn((String) value.get("name"));
                JSONObject address = (JSONObject) value.get("address");
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
                expediaPropertyBasicDao.update(expediaPropertyBasic);*/
            }
        }

    }

    public void expediaStatistics() {
        // 查询出16分的数据
        List<ExpediaDaolvMatchLab> expediaDaolvMatchLabs = expediaDaolvMatchLabDao.select16List();
        // 排查掉expediaHotelId和daolvHotelId重复的数据
        List<ExpediaDaolvMatchLab> onlyOne16List = Lists.newArrayListWithCapacity(expediaDaolvMatchLabs.size());
        Map<Integer, List<ExpediaDaolvMatchLab>> collect = expediaDaolvMatchLabs.stream().collect(Collectors.groupingBy(ExpediaDaolvMatchLab::getDaolvHotelId));
        for (List<ExpediaDaolvMatchLab> value : collect.values()) {
            if (value.size() == 1) {
                onlyOne16List.add(value.get(0));
            }
        }
        // 判断onlyOne16List中有多少已经映射
        List<String> mappingDaolvIds = zhJdJdbGjMappingDao.selectAllDaolvId();
        Map<String, Integer> daolvIdMap = onlyOne16List.stream().collect(Collectors.toMap(e -> e.getDaolvHotelId() + "", ExpediaDaolvMatchLab::getDaolvHotelId));
        long count = mappingDaolvIds.stream().filter(daolvIdMap::containsKey).count();
        System.out.println("总匹配" + expediaDaolvMatchLabs.size() + "条，双方id唯一且16分的" + onlyOne16List.size() + "条，" +
                "道旅总匹配数: " + mappingDaolvIds.size() + "条");
        System.out.println("可立即上线的expedia" + count);
    }

    public void analyzePropertyStaticFile3() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\0905\\en-US.expediacollect.propertycatalog.jsonl";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        List<ExpediaContentBasic> expediaContentBasicList = Lists.newArrayListWithCapacity(3000);
        Date now = new Date();
        for (String line : lines) {
            ExpediaContentBasic expediaContentBasic = parse(line, now);
            expediaContentBasicList.add(expediaContentBasic);
            if (expediaContentBasicList.size() == 1000) {
                expediaContentBasicDao.saveBatch(expediaContentBasicList);
                expediaContentBasicList.clear();
            }
        }
        if (expediaContentBasicList.size() > 0) {
            expediaContentBasicDao.saveBatch(expediaContentBasicList);
        }
    }

    private ExpediaContentBasic parse(String line, Date now) throws Exception {
        JSONObject jsonObject = JSON.parseObject(line);
        ExpediaContentBasic contentBasic = new ExpediaContentBasic();
        contentBasic.setHotelId((String) jsonObject.get("property_id"));
        contentBasic.setName((String) jsonObject.get("name"));
        JSONObject address = (JSONObject) jsonObject.get("address");
        if (address != null) {
            String line1 = (String) address.get("line_1");
            String line2 = (String) address.get("line_2");
            contentBasic.setAddress(line1 + (StringUtils.hasLength(line2) ? line2 : ""));
            contentBasic.setCountryCode((String) address.get("country_code"));
            contentBasic.setStateProvinceCode((String) address.get("state_province_code"));
            contentBasic.setStateProvinceName((String) address.get("state_province_name"));
            contentBasic.setCity((String) address.get("city"));
            contentBasic.setZipCode((String) address.get("postal_code"));
        }

        JSONObject ratings = (JSONObject) jsonObject.get("ratings");
        if (ratings != null) {
            JSONObject property = (JSONObject) ratings.get("property");
            if (property != null) {
                contentBasic.setStarRating(new BigDecimal((String) property.get("rating")));
            }
            JSONObject guest = (JSONObject) ratings.get("guest");
            if (guest != null) {
                contentBasic.setGuest(new BigDecimal((String) guest.get("overall")));
            }
        }

        JSONObject location = (JSONObject) jsonObject.get("location");
        if (location != null) {
            Object object = location.get("coordinates");
            if (StringUtils.hasLength(object.toString())) {
                Coordinates coordinates1 = JSON.parseObject(object.toString(), Coordinates.class);
                contentBasic.setLongitude(coordinates1.getLongitude());
                contentBasic.setLatitude(coordinates1.getLatitude());
            }
        }

        contentBasic.setTelephone((String) jsonObject.get("phone"));

        JSONObject category = (JSONObject) jsonObject.get("category");
        if (category != null) {
            contentBasic.setCategoryId(Long.valueOf((String) category.get("id")));
            contentBasic.setCategory((String) category.get("name"));
        }
        Integer rank = (Integer) jsonObject.get("rank");
        contentBasic.setRank(Long.parseLong(String.valueOf(rank)));

        JSONObject businessModel = (JSONObject) jsonObject.get("business_model");
        if (businessModel != null) {
            contentBasic.setExpediaCollect((Boolean) businessModel.get("expedia_collect"));
            contentBasic.setPropertyCollect((Boolean) businessModel.get("property_collect"));
        }

        JSONObject statistics = (JSONObject) jsonObject.get("statistics");
        if (statistics != null) {
            StringBuilder ids = new StringBuilder();
            StringBuilder values = new StringBuilder();
            for (Map.Entry<String, Object> entry : statistics.entrySet()) {
                ids.append(entry.getKey()).append(",");
                values.append(((JSONObject) entry.getValue()).get("value")).append(",");
            }
            contentBasic.setStatisticsId(ids.substring(0, ids.length() - 1));
            contentBasic.setStatisticsValues(values.substring(0, values.length() - 1));
        }

        JSONObject chain = (JSONObject) jsonObject.get("chain");
        if (chain != null) {
            contentBasic.setChainId((String) chain.get("id"));
        }

        JSONObject brand = (JSONObject) jsonObject.get("brand");
        if (brand != null) {
            contentBasic.setBrandId((String) brand.get("id"));
        }
        contentBasic.setSupplySource((String) jsonObject.get("supply_source"));

        JSONObject dates = (JSONObject) jsonObject.get("dates");
        if (dates != null) {
            contentBasic.setAddedTime(transferDateTime((String) dates.get("added")));
            contentBasic.setUpdatedTime(transferDateTime((String) dates.get("updated")));
        }
        contentBasic.setCreateTime(now);
        return contentBasic;
    }

    // 2023-11-23T09:05:37.357Z
    private Date transferDateTime(String dateTime) throws Exception {
        if (!StringUtils.hasLength(dateTime)) return null;
        dateTime = dateTime.replace("T", " ");
        dateTime = dateTime.substring(0, dateTime.indexOf("."));
        return DateUtils.parseDate(dateTime);
    }

    public void pullContentDetail() {
        // List<ExpediaContentBasic> expediaContentBasics = expediaContentBasicDao.selectNeedUpdateHotelIds();
        List<String> hotelIds = Lists.newArrayList("18254528", "526195", "193334", "10106413", "76881646", "16054819", "6347171", "8098707", "996323", "2776931", "12031020", "95802215", "17254211", "87827003", "4658434", "2443993", "89848983", "70868099", "2878", "4698614", "19002255", "259", "899349", "11710171", "1803401", "76521087", "129176", "33387167", "93691196", "18182101", "49301400", "49301416", "22297811", "21937310", "49301405", "22794", "92897619", "49301388", "49301421", "91219195", "49301371", "99049846", "27205068", "105963667", "95360484", "100643741", "9623767", "104475403", "92961563", "96657957");
        int total = hotelIds.size();
        int start = 0;
        List<CompletableFuture<ExpediaContentResult>> futures = Lists.newArrayListWithCapacity(32);
        int chunkSize = 20;
        for (int i = 0; i < total; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, total);
            for (String hotelId : hotelIds.subList(i, endIndex)) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    String en = httpUtils.pullContentEn(hotelId);
                    String zh = httpUtils.pullContentZh(hotelId);
                    boolean hasEn = StringUtils.hasLength(en) && !"{}".equals(en);
                    boolean hasZh = StringUtils.hasLength(zh) && !"{}".equals(zh);
                    ExpediaContentResult result = new ExpediaContentResult();
                    result.setHotelId(hotelId);
                    // 没英文没中文 返回null
                    if (!hasEn && !hasZh) {
                        result.setRealExist(false);
                        result.setHasZh(false);
                        return result;
                    }
                    // 有英文和中文
                    if (hasEn && hasZh) {
                        result.setRealExist(true);
                        parseEn(en, result);
                        result.setHasZh(true);
                        parseZh(zh, result);
                        return result;
                    }
                    // 有英文没中文
                    if (hasEn) {
                        result.setRealExist(true);
                        parseEn(en, result);
                        result.setHasZh(false);
                        return result;
                    }
                    // 没英文有中文
                    result.setRealExist(false);
                    result.setHasZh(true);
                    parseZh(zh, result);
                    return result;
                }, executor2));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            for (CompletableFuture<ExpediaContentResult> future : futures) {
                StopWatch watch = new StopWatch();
                watch.start();
                start++;
                String hotelId = null;
                try {
                    ExpediaContentResult result = future.get(3, TimeUnit.SECONDS);
                    hotelId = result.getHotelId();
                    ExpediaContentBasic update = new ExpediaContentBasic();
                    update.setId(result.getId());
                    BeanUtils.copyProperties(result, update);
                    expediaContentBasicDao.updateByHotelId(update);
                } catch (TimeoutException e) {
                    watch.stop();
                    log.info("酒店查询失败, 超时被跳过, 当前进度:{}/{}", start, total);
                    continue;
                } catch (Exception e) {
                    watch.stop();
                    log.info("{}酒店查询失败, 耗时:{}, 当前进度:{}/{}", hotelId, watch.getTotalTimeSeconds(), start, total);
                    log.error("错误信息:{}", Throwables.getStackTraceAsString(e));
                    continue;
                }
                watch.stop();
                log.info("{}酒店查询成功, 耗时:{}, 当前进度:{}/{}", hotelId, watch.getTotalTimeSeconds(), start, total);
            }
            futures.clear();
        }
    }

    private void parseZh(String expediaContent, ExpediaContentResult result) {
        JSONObject jsonObject = JSON.parseObject(expediaContent);
        Collection<Object> values = jsonObject.values();
        Content content = JSON.parseObject(values.stream().findFirst().get().toString(), Content.class);
        result.setName(content.getName());
        Address address = content.getAddress();
        result.setAddress(address.getLine_1() + (StringUtils.hasLength(address.getLine_2()) ? address.getLine_2() : ""));
    }

    private void parseEn(String expediaContent, ExpediaContentResult result) {
        JSONObject jsonObject = JSON.parseObject(expediaContent);
        Collection<Object> values = jsonObject.values();
        Content content = JSON.parseObject(values.stream().findFirst().get().toString(), Content.class);
        result.setFax(content.getFax());
        String themes = content.getThemes();
        if (StringUtils.hasLength(themes)) {
            result.setThemes(String.join(",", JSON.parseObject(themes).keySet()));
        }
        String spokenLanguages = content.getSpoken_languages();
        if (StringUtils.hasLength(spokenLanguages)) {
            result.setSpokenLanguages(String.join(",", JSON.parseObject(spokenLanguages).keySet()));
        }
        List<Images> images = content.getImages();
        if (CollectionUtils.isNotEmpty(images)) {
            Optional<Images> first = images.stream().filter(Images::isHero_image).findFirst();
            first.ifPresent(e -> {
                result.setHeroImageMiddle(((JSONObject) JSON.parseObject(e.getLinks()).get("350px")).get("href").toString());
            });
        }
    }

    public void pullContentPrice() {
        //List<ExpediaContentBasic> expediaContentBasics = expediaContentBasicDao.selectNeedPriceHotelIds();
        List<String> hotelIds = Lists.newArrayList("18254528", "526195", "193334", "10106413", "76881646", "16054819", "6347171", "8098707", "996323", "2776931", "12031020", "95802215", "17254211", "87827003", "4658434", "2443993", "89848983", "70868099", "2878", "4698614", "19002255", "259", "899349", "11710171", "1803401", "76521087", "129176", "33387167", "93691196", "18182101", "49301400", "49301416", "22297811", "21937310", "49301405", "22794", "92897619", "49301388", "49301421", "91219195", "49301371", "99049846", "27205068", "105963667", "95360484", "100643741", "9623767", "104475403", "92961563", "96657957");
        int total = hotelIds.size();
        int start = 0;
        List<CompletableFuture<ExpediaPriceResult>> futures = Lists.newArrayListWithExpectedSize(PRICE_CHUNK_SIZE);
        int chunkSize = PRICE_CHUNK_SIZE;
        for (int i = 0; i < total; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, total);
            for (String hotelId : hotelIds.subList(i, endIndex)) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                    //String hotelId = expediaContentBasic.getHotelId();
                    ExpediaPriceResult result = new ExpediaPriceResult();
                    //result.setId(expediaContentBasic.getId());
                    result.setHotelId(hotelId);
                    String checkIn = "2024-09-22";
                    String checkOut = "2024-09-23";
                    String price = httpUtils.pullPrice(hotelId, checkIn, checkOut);
                    boolean hasPrice = StringUtils.hasLength(price) && !"{}".equals(price) && !"[]".equals(price);
                    if (hasPrice) {
                        result.setHasPrice(true);
                        return result;
                    }
                    String packagePrice = httpUtils.pullPricePackage(hotelId, checkIn, checkOut);
                    boolean hasPackagePrice = StringUtils.hasLength(packagePrice) && !"{}".equals(packagePrice) && !"[]".equals(packagePrice);
                    if (hasPackagePrice) {
                        result.setHasPrice(true);
                        return result;
                    }
                    String checkIn2 = "2024-10-13";
                    String checkOut2 = "2024-10-14";
                    String price2 = httpUtils.pullPrice(hotelId, checkIn2, checkOut2);
                    boolean hasPrice2 = StringUtils.hasLength(price2) && !"{}".equals(price2) && !"[]".equals(price2);
                    if (hasPrice2) {
                        result.setHasPrice(true);
                        return result;
                    }
                    String packagePrice2 = httpUtils.pullPricePackage(hotelId, checkIn2, checkOut2);
                    boolean hasPackagePrice2 = StringUtils.hasLength(packagePrice2) && !"{}".equals(packagePrice2) && !"[]".equals(packagePrice2);
                    if (hasPackagePrice2) {
                        result.setHasPrice(true);
                        return result;
                    }
                    result.setHasPrice(false);
                    return result;
                }, executor3));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            for (CompletableFuture<ExpediaPriceResult> future : futures) {
                start++;
                String hotelId = null;
                try {
                    ExpediaPriceResult result = future.get(3, TimeUnit.SECONDS);
                    hotelId = result.getHotelId();
                    ExpediaContentBasic update = new ExpediaContentBasic();
                    update.setHotelId(hotelId);
                    update.setHasPrice(result.getHasPrice());
                    expediaContentBasicDao.updatePriceByHotelId(update);
                } catch (TimeoutException e) {
                    log.info("酒店查价失败, 超时被跳过, 当前进度:{}/{}", start, total);
                    continue;
                } catch (Exception e) {
                    log.info("{}酒店查价失败, 当前进度:{}/{}", hotelId, start, total);
                    log.error("错误信息:{}", Throwables.getStackTraceAsString(e));
                    continue;
                }
                log.info("{}酒店查价成功, 当前进度:{}/{}", hotelId, start, total);
            }
            futures.clear();
        }
    }

    public void expediaV1Up() {
        // 查询出16分的数据
        List<ExpediaDaolvMatchLab> expediaDaolvMatchLabs = expediaDaolvMatchLabDao.select16List();
        // 排查掉expediaHotelId和daolvHotelId重复的数据
        List<ExpediaDaolvMatchLab> onlyOne16List = Lists.newArrayListWithCapacity(expediaDaolvMatchLabs.size());
        Map<Integer, List<ExpediaDaolvMatchLab>> collect = expediaDaolvMatchLabs.stream().collect(Collectors.groupingBy(ExpediaDaolvMatchLab::getDaolvHotelId));
        for (List<ExpediaDaolvMatchLab> value : collect.values()) {
            if (value.size() == 1) {
                onlyOne16List.add(value.get(0));
            }
        }
        // 判断onlyOne16List中有多少已经映射
        List<String> mappingDaolvIds = zhJdJdbGjMappingDao.selectAllDaolvId();
        Map<String, String> daolvMap = mappingDaolvIds.stream().collect(Collectors.toMap(e -> e, e -> e));

        List<String> expediaHotelIdList = onlyOne16List.stream().filter(e -> daolvMap.containsKey(e.getDaolvHotelId() + "")).map(ExpediaDaolvMatchLab::getExpediaHotelId).toList();
        int size = expediaHotelIdList.size();
        log.info("总匹配{}条，双方id唯一且16分的{}条,道旅总匹配数:{}", expediaDaolvMatchLabs.size(), onlyOne16List.size(), expediaHotelIdList.size());

        int start = 0;
        for (int j = 0; j < size; j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = expediaHotelIdList.subList(start, j);
                expediaContentBasicDao.updateV1Sale(list);
                start = j;
            }
        }
        List<String> list = expediaHotelIdList.subList(start, size);
        if (CollectionUtils.isNotEmpty(list)) {
            expediaContentBasicDao.updateV1Sale(list);
        }
    }

    public void checkMultiPrice() {
        String checkIn = "2024-09-22";
        String checkOut = "2024-09-23";
        List<String> countryCodes = Lists.newArrayList("CN", "SG", "JP", "GB", "FR", "US", "DE", "CH", "AU", "IL", "ES", "NL", "BR", "TH", "CA", "CN", "RU");
        List<String> hotelIds = Lists.newArrayList("101340267", "11723", "13375806", "13407274", "15228437", "15239", "17405012", "19663253", "27780006", "807460");//,"11723","13375806","13407274","15228437","15239","17405012","19663253","27780006","807460"

        String file = "C:\\wst_han\\打杂\\expedia\\test1.xlsx";
        ExcelWriter excelWriter = EasyExcel.write(file).build();

        for (String hotelId : hotelIds) {
            List<MultiPrice> all = Lists.newArrayList();
            for (String countryCode : countryCodes) {
                String priceString = httpUtils.pullPrice2(hotelId, checkIn, checkOut, countryCode);
                boolean hasPrice = StringUtils.hasLength(priceString) && !"{}".equals(priceString) && !"[]".equals(priceString);
                if (!hasPrice) continue;
                List<MultiPrice> priceList = analyzePrice(priceString, hotelId, countryCode);
                all.addAll(priceList);
            }
            log.info("{}酒店查询组装完毕", hotelId);
            List<MultiPriceExport> exports = Lists.newArrayList();
            List<String> rateIds = all.stream().map(MultiPrice::getRateId).distinct().toList();
            Map<String, List<MultiPrice>> countryCodeMap = all.stream().collect(Collectors.groupingBy(MultiPrice::getCountryCode));

            MultiPriceExport export1 = new MultiPriceExport();
            export1.setCountryCode(null);
            for (int j = 0; j < rateIds.size(); j++) {
                setValue(export1, j + 1, rateIds.get(j));
            }
            exports.add(export1);
            for (Map.Entry<String, List<MultiPrice>> entry : countryCodeMap.entrySet()) {
                String key = entry.getKey();
                List<MultiPrice> priceList = entry.getValue();
                MultiPriceExport export = new MultiPriceExport();
                export.setCountryCode(key);
                for (MultiPrice multiPrice : priceList) {
                    int i = rateIds.indexOf(multiPrice.getRateId());
                    setValue(export, i + 1, multiPrice.getPrice());
                }
                exports.add(export);
            }
            WriteSheet writeSheet = EasyExcel.writerSheet(hotelId).head(MultiPriceExport.class).build();
            excelWriter.write(exports, writeSheet);
        }
        excelWriter.finish();
    }

    private void setValue(MultiPriceExport export, int i, String value) {
        if (i > 17) return;
        try {
            Field field = export.getClass().getDeclaredField("rateId" + i);
            field.setAccessible(true);
            field.set(export, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<MultiPrice> analyzePrice(String json, String hotelId, String countryCode) {
        JSONObject jsonObject = (JSONObject) JSON.parseArray(json).get(0);
        JSONArray rooms = (JSONArray) jsonObject.get("rooms");
        List<MultiPrice> multiPrices = Lists.newArrayList();
        for (Object room : rooms) {
            JSONObject roomJson = (JSONObject) room;
            String roomId = (String) roomJson.get("id");
            String roomName = (String) roomJson.get("room_name");
            JSONArray rates = (JSONArray) roomJson.get("rates");
            for (Object rate : rates) {
                JSONObject rateJson = (JSONObject) rate;
                String rateId = (String) rateJson.get("id");
                JSONObject occupancyPricing = (JSONObject) rateJson.get("occupancy_pricing");
                JSONObject occupancyPricing1 = (JSONObject) occupancyPricing.get("1");
                JSONObject totals = (JSONObject) occupancyPricing1.get("totals");
                JSONObject inclusive = (JSONObject) totals.get("inclusive");
                JSONObject requestCurrency = (JSONObject) inclusive.get("request_currency");
                String price = requestCurrency.get("value").toString();
                MultiPrice multiPrice = new MultiPrice();
                multiPrice.setHotelId(hotelId);
                multiPrice.setCountryCode(countryCode);
                multiPrice.setRoomId(roomId);
                multiPrice.setRoomName(roomName);
                multiPrice.setRateId(rateId);
                multiPrice.setPrice(price);
                multiPrices.add(multiPrice);
            }
        }
        return multiPrices;
    }

    public static void main(String[] args) throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\test.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        JSONObject jsonObject = (JSONObject) JSON.parseArray(json).get(0);
        JSONArray array = (JSONArray) jsonObject.get("rooms");
        JSONObject object = (JSONObject) array.get(0);
        JSONArray rates = (JSONArray) object.get("rates");
        JSONObject object1 = (JSONObject) rates.get(0);
        JSONObject total = (JSONObject) ((JSONObject) ((JSONObject) object1.get("occupancy_pricing")).get("1")).get("totals");
        JSONObject inclusive = (JSONObject) total.get("inclusive");
        JSONObject requestCurrency = (JSONObject) inclusive.get("request_currency");
        String price = (String) requestCurrency.get("value");
        System.out.println(price);
        System.out.println(object1.get("id"));
    }

    public void pullCheckCountry() throws Exception {
        List<ExpediaCountry> expediaRegions = Lists.newArrayListWithCapacity(256);
        int page = 0;
        Integer load;
        String nextPageUrl = null;
        do {
            StopWatch watch = new StopWatch();
            watch.start();
            if ("".equals(nextPageUrl)) break;
            ExpediaResponse response = httpUtils.pullRegions(nextPageUrl);
            watch.stop();
            log.info("获取第{}页内容, cost[s]: {},请求路径:{}", page, watch.getTotalTimeSeconds(), nextPageUrl);
            String body = response.getBody();
            try {
                List<Region> regionList = JSON.parseArray(body, Region.class);
                if (CollectionUtils.isNotEmpty(regionList)) {
                    for (Region region : regionList) {
                        ExpediaCountry expediaRegion = new ExpediaCountry();
                        expediaRegion.setExpediaId(region.getId());
                        expediaRegion.setName(region.getName());
                        expediaRegions.add(expediaRegion);
                    }
                }
            } catch (Exception e) {
                log.info("转换异常{}", Throwables.getStackTraceAsString(e));
            }
            load = response.getLoad();
            nextPageUrl = response.getNextPageUrl();
            page++;
        } while (load > 0);
        for (ExpediaCountry expediaRegion : expediaRegions) {
            expediaCountryDao.update(expediaRegion);
        }
    }

    public void pullRegionsV2() throws Exception {
        List<ExpediaCountry> expediaCountries = expediaCountryDao.selectAll();
        boolean stop = false;
        for (ExpediaCountry expediaCountry : expediaCountries) {
            if (stop) break;
            String countryCode = expediaCountry.getCode();
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
                            expediaRegion.setNameFullEn(region.getName_full());
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

    public void finishCountry() {
        List<ExpediaCountry> expediaCountries = expediaCountryDao.selectAll();
        List<CompletableFuture<ExpediaCountry>> futures = Lists.newArrayListWithCapacity(2 << 6);
        for (ExpediaCountry expediaCountry : expediaCountries) {
            String expediaId = expediaCountry.getExpediaId();
            futures.add(CompletableFuture.supplyAsync(() -> httpUtils.pullCountryCode(expediaId), executor3));
        }
        for (CompletableFuture<ExpediaCountry> future : futures) {
            try {
                ExpediaCountry country = future.get();
                expediaCountryDao.updateCountryCode(country.getExpediaId(), country.getCode());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void finishRegionsV2() {
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

    public void pullProperty() {
        List<ExpediaCountry> expediaCountries = expediaCountryDao.selectAllCode();
        List<String> skip = Lists.newArrayList("AF", "AU", "LR", "LY", "LT", "LU", "MO", "MK", "MG", "MW", "MY", "MV", "AT", "ML", "MT", "GL", "MP", "MQ", "AM", "AS", "MR", "BT", "BY", "ER", "GE", "KG", "LI", "MD", "MH", "MN", "NU", "TJ", "TM", "UZ", "SI", "BA", "MU", "YT", "MX", "BV", "IO", "CX", "CC", "FK", "TF", "HM", "FO", "IM", "KP", "PN", "PM", "SM", "SH", "SJ", "TK", "VA", "WF", "PS", "FM", "AZ", "MC", "MS", "MA", "MZ", "MM", "NA", "NR", "NP", "NL", "GG", "BS", "KN", "NC", "PG", "NZ", "NI", "NE", "NG", "NF", "NO", "OM", "BH", "PK", "PW", "PA", "PY", "PE", "PH", "PL", "PT", "PR", "QA", "BD", "RE", "RO", "RU", "RW", "WS", "ST", "SA", "SN", "SC", "BB", "SL", "SG", "SK", "SB", "SO", "ZA", "ES", "LK", "LC", "VC", "BE", "SD", "SR", "SZ", "SE", "CH", "SY", "TW", "TZ", "TH", "TG", "BZ", "TO", "TT", "TN", "TR", "TC", "TV", "VI", "UG", "UA", "AE", "BJ", "GB", "UY", "VU", "VE", "VN", "YE", "RS", "CD", "ZM", "AL", "BM", "ZW");
        boolean stop = false;
        for (ExpediaCountry expediaCountry : expediaCountries) {
            if (stop) break;
            String countryCode = expediaCountry.getCode();
            if (skip.contains(countryCode)) continue;
            List<ExpediaRegionsProperty> expediaRegionsProperties = Lists.newArrayList();
            int page = 0;
            Integer load;
            String nextPageUrl = countryCode.equals("US") ? "https://test.ean.com/v3/regions?token=Q11RF1Vda1JREAQQCQMFVgpRDVQCCFcKVFAPAQIMVh4DSRZaR1wXXgZUC1AZVgobC1BhVFcQUCACDEcHc1cGHAUCAgAGBwUAATgSBltBD0JKSGpWWwAAQQRABXQQClRtY0EEARJUd0VRWgcNQVxXEgRDACcUBQsVQQxDU0YSSjtQUkMWAgcWUXMeCgNaBkVSXgQEVAwVNGtHdglXVw0QR1gIB11RBFYGAVgB" : null;
            do {
                StopWatch watch = new StopWatch();
                watch.start();
                if ("".equals(nextPageUrl)) break;
                ExpediaResponse response = httpUtils.pullProperty(nextPageUrl, countryCode);
                watch.stop();
                log.info("国家{}，获取第{}页内容, cost[s]: {},请求路径:{}", countryCode, page, watch.getTotalTimeSeconds(), nextPageUrl);
                String body = response.getBody();
                try {
                    List<Region> regionList = JSON.parseArray(body, Region.class);
                    if (CollectionUtils.isNotEmpty(regionList)) {
                        for (Region region : regionList) {
                            List<String> propertyIds = region.getProperty_ids();
                            if (CollectionUtils.isNotEmpty(propertyIds)) {
                                for (String propertyId : propertyIds) {
                                    ExpediaRegionsProperty expediaRegionsProperty = new ExpediaRegionsProperty();
                                    expediaRegionsProperty.setRegionId(region.getId());
                                    expediaRegionsProperty.setPropertyId(propertyId);
                                    expediaRegionsProperties.add(expediaRegionsProperty);
                                }
                            }
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
                if (CollectionUtils.isNotEmpty(expediaRegionsProperties)) {
                    expediaRegionsPropertyDao.saveBatch(expediaRegionsProperties);
                    expediaRegionsProperties.clear();
                }
                page++;
            } while (load > 0);
            log.info("国家{} finish ", countryCode);
        }
    }

    public void analyzeProperty() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\mainHotel\\2024 YTD_Top Selling CL 0901 reduced.csv";
        InputStream inputStream = new FileInputStream(fileName);
        List<MainHotelImport> imports = Lists.newArrayListWithCapacity(2 << 17);
        EasyExcel.read(inputStream, MainHotelImport.class, new PageReadListener<MainHotelImport>(imports::addAll, 1000)).headRowNumber(1).sheet().doRead();

        int start = 0;
        for (int j = 0; j < imports.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<MainHotelImport> list = imports.subList(start, j);
                expediaContentBasicDao.updateMainPrice(list);
                start = j;
            }
        }
        List<MainHotelImport> list = imports.subList(start, imports.size());
        if (CollectionUtils.isNotEmpty(list)) {
            expediaContentBasicDao.updateMainPrice(list);
        }
    }

    public void match() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\mainHotel\\2024 YTD_Top Selling CL 0901 reduced.csv";
        InputStream inputStream = new FileInputStream(fileName);
        List<MainHotelImport> imports = Lists.newArrayListWithCapacity(2 << 17);
        EasyExcel.read(inputStream, MainHotelImport.class, new PageReadListener<MainHotelImport>(imports::addAll, 1000)).headRowNumber(1).sheet().doRead();

        List<String> hotelIds = expediaContentBasicDao.selectAllHotelIds();
        Map<String, String> map = hotelIds.stream().collect(Collectors.toMap(e -> e, e -> e));
        List<MainHotelImport> imports1 = imports.stream().filter(e -> !map.containsKey(e.getPropertyId() + "")).toList();
        System.out.println(imports1.size());
        log.info("{}, 找不到的hotelIds: {}", imports1.size(), imports1.stream().map(MainHotelImport::getPropertyId).collect(Collectors.toList()));

    }

    public void newHotel() {
        List<String> newList = Lists.newArrayList("1135667", "8240", "18254528", "4776374", "526195", "6164018", "193334", "10106413", "19410007", "6815", "65266977", "42001469", "35155200", "76881646", "16054819", "22166150", "984887", "6347171", "18354353", "2270671", "2806148", "973324", "102802105", "99222045", "86118312", "35813636", "92971845", "19429959", "8098707", "996323", "22161302", "1826208", "2776931", "29857237", "3968932", "1337897", "30490876", "12031020", "39194629", "95802215", "15676037", "48208166", "17254211", "10982557", "7762407", "87827003", "42267764", "1828475", "1039730", "4658434", "2443993", "1887719", "5316431", "10619", "59667356", "71657643", "12727505", "89848983", "70868099", "57417336", "103913498", "15369628", "2878", "16377866", "11609317", "99613855", "103797382", "6607252", "195", "4485419", "1784067", "12930", "151228", "15415167", "36305867", "4724225", "4698614", "107841789", "19002255", "18406720", "61418210", "73458817", "259", "89465124", "899349", "528580", "11710171", "71422368", "12611785", "100150272", "16138021", "99767698", "13064933", "39970792", "1803401", "76521087", "19312474", "28809409", "9438646", "129176", "4482139", "3853825", "33387167", "104361357", "92613997", "38719388", "39151617", "100885865", "565288", "1058928", "21495841", "6816656", "93691196", "82675287", "98473666", "33331173", "18182101", "49301400", "23401402", "59567213", "8157004", "5224587", "4125655", "11667650", "49301416", "10582871", "39153676", "9106876", "22297811", "36299109", "15566366", "36373261", "96490811", "86750807", "3905428", "21937310", "6126979", "49301405", "29846029", "73754105", "22794", "22954973", "36249728", "18588278", "92897619", "95752289", "49301388", "5770035", "87226090", "89358675", "102187360", "30333063", "89380929", "16716451", "104731286", "91286881", "49301421", "96455727", "91219195", "18034064", "24151571", "71667128", "42133115", "49301371", "107602817", "1580314", "92010548", "29092961", "48446569", "96582987", "37039608", "3720752", "44563127", "99049846", "27205068", "104198492", "105963667", "90675127", "95360484", "20186531", "32773829", "105251791", "82697178", "71579123", "102425254", "93679500", "35853008", "32021282", "35216392", "89521187", "76226797", "100581985", "100643741", "89295414", "93075809", "47069413", "26811561", "23206502", "93831843", "9623767", "95525912", "92770578", "22681830", "107128426", "21809531", "37213220", "32460298", "101938735", "6471592", "101887165", "70845066", "104475403", "34075295", "100604119", "82945495", "39516102", "96749523", "495", "34309516", "31152261", "92961563", "25150650", "32614314", "48811209", "76239462", "98472660", "94930760", "90250216", "92471277", "25143165", "72398772", "104284511", "89025770", "101542139", "106220673", "96657957", "31976354", "100392075", "5771806", "76852954", "107508058", "106426534", "30089736", "9798416", "100477082", "71970339", "91423741", "83216024", "76886469", "103431575", "105591375", "106874563", "91934083", "101306278", "102613985", "101166841", "46310764");
        String language = "en-US";
        List<String> cantFoundList = Lists.newArrayListWithCapacity(newList.size());
        List<String> insertList = Lists.newArrayListWithCapacity(newList.size());
        for (String hotelId : newList) {
            String body = httpUtils.pullContent(hotelId, language);
            if (body == null || "{}".equals(body)) {
                cantFoundList.add(hotelId);
                continue;
            }
            insertList.add(hotelId);
        }
        log.info("{}, 找不到的hotelIds: {}", cantFoundList.size(), new ArrayList<>(cantFoundList));
        log.info("{}, 需要插入的hotelIds: {}", insertList.size(), new ArrayList<>(insertList));
    }

    public void addHotel() throws Exception {
        List<String> list = Lists.newArrayList("18254528", "526195", "193334", "10106413", "76881646", "16054819", "6347171", "8098707", "996323", "2776931", "12031020", "95802215", "17254211", "87827003", "4658434", "2443993", "89848983", "70868099", "2878", "4698614", "19002255", "259", "899349", "11710171", "1803401", "76521087", "129176", "33387167", "93691196", "18182101", "49301400", "49301416", "22297811", "21937310", "49301405", "22794", "92897619", "49301388", "49301421", "91219195", "49301371", "99049846", "27205068", "105963667", "95360484", "100643741", "9623767", "104475403", "92961563", "96657957");
        String language = "en-US";
        List<ExpediaContentBasic> expediaContentBasicList = Lists.newArrayListWithCapacity(2 << 5);
        Date now = new Date();
        for (String hotelId : list) {
            String body = httpUtils.pullContent(hotelId, language);
            ExpediaContentBasic expediaContentBasic = parse(body, now, hotelId);
            expediaContentBasicList.add(expediaContentBasic);
        }
        expediaContentBasicDao.saveBatch(expediaContentBasicList);
    }

    private ExpediaContentBasic parse(String line, Date now, String hotelId) throws Exception {
        JSONObject jsonObject1 = JSON.parseObject(line);
        JSONObject jsonObject = (JSONObject) jsonObject1.get(hotelId);
        ExpediaContentBasic contentBasic = new ExpediaContentBasic();
        contentBasic.setHotelId((String) jsonObject.get("property_id"));
        contentBasic.setName((String) jsonObject.get("name"));
        JSONObject address = (JSONObject) jsonObject.get("address");
        if (address != null) {
            String line1 = (String) address.get("line_1");
            String line2 = (String) address.get("line_2");
            contentBasic.setAddress(line1 + (StringUtils.hasLength(line2) ? line2 : ""));
            contentBasic.setCountryCode((String) address.get("country_code"));
            contentBasic.setStateProvinceCode((String) address.get("state_province_code"));
            contentBasic.setStateProvinceName((String) address.get("state_province_name"));
            contentBasic.setCity((String) address.get("city"));
            contentBasic.setZipCode((String) address.get("postal_code"));
        }

        JSONObject ratings = (JSONObject) jsonObject.get("ratings");
        if (ratings != null) {
            JSONObject property = (JSONObject) ratings.get("property");
            if (property != null) {
                contentBasic.setStarRating(new BigDecimal((String) property.get("rating")));
            }
            JSONObject guest = (JSONObject) ratings.get("guest");
            if (guest != null) {
                contentBasic.setGuest(new BigDecimal((String) guest.get("overall")));
            }
        }

        JSONObject location = (JSONObject) jsonObject.get("location");
        if (location != null) {
            Object object = location.get("coordinates");
            if (StringUtils.hasLength(object.toString())) {
                Coordinates coordinates1 = JSON.parseObject(object.toString(), Coordinates.class);
                contentBasic.setLongitude(coordinates1.getLongitude());
                contentBasic.setLatitude(coordinates1.getLatitude());
            }
        }

        contentBasic.setTelephone((String) jsonObject.get("phone"));

        JSONObject category = (JSONObject) jsonObject.get("category");
        if (category != null) {
            contentBasic.setCategoryId(Long.valueOf((String) category.get("id")));
            contentBasic.setCategory((String) category.get("name"));
        }
        Integer rank = (Integer) jsonObject.get("rank");
        contentBasic.setRank(Long.parseLong(String.valueOf(rank)));

        JSONObject businessModel = (JSONObject) jsonObject.get("business_model");
        if (businessModel != null) {
            contentBasic.setExpediaCollect((Boolean) businessModel.get("expedia_collect"));
            contentBasic.setPropertyCollect((Boolean) businessModel.get("property_collect"));
        }

        JSONObject statistics = (JSONObject) jsonObject.get("statistics");
        if (statistics != null) {
            StringBuilder ids = new StringBuilder();
            StringBuilder values = new StringBuilder();
            for (Map.Entry<String, Object> entry : statistics.entrySet()) {
                ids.append(entry.getKey()).append(",");
                values.append(((JSONObject) entry.getValue()).get("value")).append(",");
            }
            contentBasic.setStatisticsId(ids.substring(0, ids.length() - 1));
            contentBasic.setStatisticsValues(values.substring(0, values.length() - 1));
        }

        JSONObject chain = (JSONObject) jsonObject.get("chain");
        if (chain != null) {
            contentBasic.setChainId((String) chain.get("id"));
        }

        JSONObject brand = (JSONObject) jsonObject.get("brand");
        if (brand != null) {
            contentBasic.setBrandId((String) brand.get("id"));
        }
        contentBasic.setSupplySource((String) jsonObject.get("supply_source"));

        JSONObject dates = (JSONObject) jsonObject.get("dates");
        if (dates != null) {
            contentBasic.setAddedTime(transferDateTime((String) dates.get("added")));
            contentBasic.setUpdatedTime(transferDateTime((String) dates.get("updated")));
        }
        contentBasic.setCreateTime(now);
        return contentBasic;
    }

    public void compareHotel() {
        List<String> hotelIds = expediaContentBasicDao.selectAllHotelIds();
        Map<String, String> contentMap = hotelIds.stream().collect(Collectors.toMap(e -> e, e -> e));

        List<String> propertyList = expediaPropertyBasicDao.selectAllHotelIds();
        Map<String, String> propertyMap = propertyList.stream().collect(Collectors.toMap(e -> e, e -> e));

        List<String> found1 = Lists.newArrayList();
        for (String propertyId : propertyList) {
            if (!contentMap.containsKey(propertyId)) {
                found1.add(propertyId);
            }
        }

        List<String> found2 = Lists.newArrayList();
        for (String hotelId : hotelIds) {
            if (!propertyMap.containsKey(hotelId)) {
                found2.add(hotelId);
            }
        }
        log.info("Content没有， Property有数量：{}, 具体：{}", found1.size(), new ArrayList<>(found1));
        log.info("Property没有，Content有数量：{}, 具体：{}", found2.size(), new ArrayList<>(found2));
    }

    public void exportHotel() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\mainHotel\\2024 YTD_Top Selling CL 0901 reduced.csv";
        InputStream inputStream = new FileInputStream(fileName);
        List<ExpediaMainHotelResult> imports = Lists.newArrayListWithCapacity(2 << 17);
        EasyExcel.read(inputStream, ExpediaMainHotelResult.class, new PageReadListener<ExpediaMainHotelResult>(imports::addAll, 1000)).headRowNumber(1).sheet().doRead();

        Set<String> notFoundSet = Sets.newHashSet("1135667", "8240", "4776374", "6164018", "19410007", "6815", "65266977", "42001469", "35155200", "22166150", "984887", "18354353", "2270671", "2806148", "973324", "102802105", "99222045", "86118312", "35813636", "92971845", "19429959", "22161302", "1826208", "29857237", "3968932", "1337897", "30490876", "39194629", "15676037", "48208166", "10982557", "7762407", "42267764", "1828475", "1039730", "1887719", "5316431", "10619", "59667356", "71657643", "12727505", "57417336", "103913498", "15369628", "16377866", "11609317", "99613855", "103797382", "6607252", "195", "4485419", "1784067", "12930", "151228", "15415167", "36305867", "4724225", "107841789", "18406720", "61418210", "73458817", "89465124", "528580", "71422368", "12611785", "100150272", "16138021", "99767698", "13064933", "39970792", "19312474", "28809409", "9438646", "4482139", "3853825", "104361357", "92613997", "38719388", "39151617", "100885865", "565288", "1058928", "21495841", "6816656", "82675287", "98473666", "33331173", "23401402", "59567213", "8157004", "5224587", "4125655", "11667650", "10582871", "39153676", "9106876", "36299109", "15566366", "36373261", "96490811", "86750807", "3905428", "6126979", "29846029", "73754105", "22954973", "36249728", "18588278", "95752289", "5770035", "87226090", "89358675", "102187360", "30333063", "89380929", "16716451", "104731286", "91286881", "96455727", "18034064", "24151571", "71667128", "42133115", "107602817", "1580314", "92010548", "29092961", "48446569", "96582987", "37039608", "3720752", "44563127", "104198492", "90675127", "20186531", "32773829", "105251791", "82697178", "71579123", "102425254", "93679500", "35853008", "32021282", "35216392", "89521187", "76226797", "100581985", "89295414", "93075809", "47069413", "26811561", "23206502", "93831843", "95525912", "92770578", "22681830", "107128426", "21809531", "37213220", "32460298", "101938735", "6471592", "101887165", "70845066", "34075295", "100604119", "82945495", "39516102", "96749523", "495", "34309516", "31152261", "25150650", "32614314", "48811209", "76239462", "98472660", "94930760", "90250216", "92471277", "25143165", "72398772", "104284511", "89025770", "101542139", "106220673", "31976354", "100392075", "5771806", "76852954", "107508058", "106426534", "30089736", "9798416", "100477082", "71970339", "91423741", "83216024", "76886469", "103431575", "105591375", "106874563", "91934083", "101306278", "102613985", "101166841", "46310764");
        List<ExpediaContentBasic> matchList = expediaContentBasicDao.selectMatchList();
        Map<String, ExpediaContentBasic> matchMap = matchList.stream().collect(Collectors.toMap(ExpediaContentBasic::getHotelId, Function.identity()));
        List<ExpediaContentBasic> notMatchList = expediaContentBasicDao.selectNotMatchList();
        Map<String, ExpediaContentBasic> notMatchMap = notMatchList.stream().collect(Collectors.toMap(ExpediaContentBasic::getHotelId, Function.identity()));

        /*List<String> expediaHotelIds = notMatchList.stream().map(ExpediaContentBasic::getHotelId).toList();
        List<ExpediaDaolvMatchLab> labs = expediaDaolvMatchLabDao.selectMatchDidaId(expediaHotelIds);
        Map<String, Integer> mmmap = labs.stream().collect(Collectors.toMap(ExpediaDaolvMatchLab::getExpediaHotelId, ExpediaDaolvMatchLab::getDaolvHotelId));
        List<Integer> integers = labs.stream().map(ExpediaDaolvMatchLab::getDaolvHotelId).distinct().toList();
        List<JdJdbDaolv> jdJdbDaolvs = jdJdbDaolvDao.selectListByIdsV2(integers.subList(0, 5000));
        jdJdbDaolvs.addAll(jdJdbDaolvDao.selectListByIdsV2(integers.subList(5000, 10000)));
        jdJdbDaolvs.addAll(jdJdbDaolvDao.selectListByIdsV2(integers.subList(10000, 15000)));
        jdJdbDaolvs.addAll(jdJdbDaolvDao.selectListByIdsV2(integers.subList(15000, integers.size())));
        Map<Integer, JdJdbDaolv> didaMap = jdJdbDaolvs.stream().collect(Collectors.toMap(JdJdbDaolv::getId, Function.identity()));*/

        System.out.println("start");
        for (ExpediaMainHotelResult result : imports) {
            String hotelId = result.getPropertyId();
            if (notFoundSet.contains(hotelId)) {
                result.setStatus("illegal hotel id");
                continue;
            }
            if (matchMap.containsKey(hotelId)) {
                result.setStatus("dida not sale");
                ExpediaContentBasic expediaContentBasic = matchMap.get(hotelId);
                result.setName(expediaContentBasic.getNameEn());
                result.setAddress(expediaContentBasic.getAddressEn());
                result.setCountryCode(expediaContentBasic.getCountryCode());
                result.setCity(expediaContentBasic.getCity());
                result.setState_province_code(expediaContentBasic.getStateProvinceCode());
                result.setState_province_name(expediaContentBasic.getStateProvinceName());
                result.setLatitude(expediaContentBasic.getLatitude());
                result.setLongitude(expediaContentBasic.getLongitude());
                continue;
            }
            if (notMatchMap.containsKey(hotelId)) {
                result.setStatus("not match");
                ExpediaContentBasic expediaContentBasic = notMatchMap.get(hotelId);
                result.setName(expediaContentBasic.getNameEn());
                result.setAddress(expediaContentBasic.getAddressEn());
                result.setCountryCode(expediaContentBasic.getCountryCode());
                result.setCity(expediaContentBasic.getCity());
                result.setState_province_code(expediaContentBasic.getStateProvinceCode());
                result.setState_province_name(expediaContentBasic.getStateProvinceName());
                result.setLatitude(expediaContentBasic.getLatitude());
                result.setLongitude(expediaContentBasic.getLongitude());
                continue;
                /*Integer didaId = mmmap.get(hotelId);
                JdJdbDaolv jdJdbDaolv = didaMap.get(didaId);
                result.setHotelName(jdJdbDaolv.getName());
                result.setHotelAddress(jdJdbDaolv.getAddress());
                result.setHotelCountryCode(jdJdbDaolv.getCountryCode());
                result.setHotelCity(jdJdbDaolv.getCityName());
                result.setHotelLatitude(jdJdbDaolv.getLatitude());
                result.setHotelLongitude(jdJdbDaolv.getLongitude());*/
            }
            result.setStatus("sale");
        }
        String file = "C:\\wst_han\\打杂\\expedia\\mainHotel\\expedia_v1_sale.xlsx";
        EasyExcel.write(file, ExpediaMainHotelResult.class).sheet("expedia-v1-sale-status").doWrite(imports);
        System.out.println("finish");
    }

    public void exportHotel2() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\expedia\\mainHotel\\expedia_v1_sale.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<ExpediaMainHotelResult> imports = Lists.newArrayListWithCapacity(2 << 17);
        EasyExcel.read(inputStream, ExpediaMainHotelResult.class, new PageReadListener<ExpediaMainHotelResult>(imports::addAll, 1000)).headRowNumber(1).sheet().doRead();
        for (ExpediaMainHotelResult result : imports) {
            String status = result.getStatus();
            if ("dida not sale".equals(status)) {
                JdJdbDaolv jdJdbDaolv = jdJdbDaolvDao.selectInfoByExpediaId(result.getPropertyId());
                result.setHotelId(jdJdbDaolv.getId());
                result.setHotelName(jdJdbDaolv.getName());
                result.setHotelAddress(jdJdbDaolv.getAddress());
                result.setHotelCountryCode(jdJdbDaolv.getCountryCode());
                result.setHotelCity(jdJdbDaolv.getCityName());
                result.setHotelLatitude(jdJdbDaolv.getLatitude());
                result.setHotelLongitude(jdJdbDaolv.getLongitude());
            }
        }
        String file = "C:\\wst_han\\打杂\\expedia\\mainHotel\\expedia_v1_sale2.xlsx";
        EasyExcel.write(file, ExpediaMainHotelResult.class).sheet("expedia-v1-sale-status").doWrite(imports);
    }
}
