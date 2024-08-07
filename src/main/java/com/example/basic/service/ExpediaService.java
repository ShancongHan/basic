package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.*;
import com.example.basic.domain.Content;
import com.example.basic.domain.ExpediaResponse;
import com.example.basic.domain.Region;
import com.example.basic.domain.to.*;
import com.example.basic.entity.*;
import com.example.basic.entity.ExpediaContent;
import com.example.basic.utils.HttpUtils;
import com.example.basic.utils.IOUtils;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
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
    private HttpUtils httpUtils;

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
}
