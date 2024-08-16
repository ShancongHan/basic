package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.*;
import com.example.basic.domain.CalculateResult;
import com.example.basic.domain.Content;
import com.example.basic.domain.ExpediaResponse;
import com.example.basic.domain.Region;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
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

    private static final Integer CORE_POOL_SIZE = 200;
    private static final Integer MAXIMUM_POOL_SIZE = 250;

    private static final Executor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(3000));

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
            Throwables.getStackTraceAsString(e);
        }
        expediaChainBrandsDao.saveBatch(expediaChainBrandsList);
    }

    private void transferBody3(List<ExpediaChainBrands> expediaChainBrandsList, String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            ExpediaChainBrands expediaChainBrands = new ExpediaChainBrands();
            JSONObject value = (JSONObject) entry.getValue();
            expediaChainBrands.setChainId((String) value.get("id"));
            expediaChainBrands.setName((String) value.get("name"));
            JSONObject object = (JSONObject) value.get("brands");

            StringBuilder ids = new StringBuilder();
            StringBuilder names = new StringBuilder();
            for (Map.Entry<String, Object> entry1 : object.entrySet()) {
                JSONObject value1 = (JSONObject) entry1.getValue();
                ids.append(value1.get("id")).append(",");
                names.append(value1.get("name")).append(",");
            }
            expediaChainBrands.setBrandsId(ids.substring(0, ids.length() - 1));
            expediaChainBrands.setBrandsName(names.substring(0, names.length() - 1));
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
        Map<String, Integer> daolvIdMap = onlyOne16List.stream().collect(Collectors.toMap(e->e.getDaolvHotelId() + "", ExpediaDaolvMatchLab::getDaolvHotelId));
        long count = mappingDaolvIds.stream().filter(daolvIdMap::containsKey).count();
        System.out.println("总匹配" + expediaDaolvMatchLabs.size() + "条，双方id唯一切16分的" + onlyOne16List.size() + "条，" +
                "道旅总匹配数: " + mappingDaolvIds.size() + "条");
        System.out.println("可立即上线的expedia" + count);
    }
}
