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
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    public void pullContent() throws Exception {
        List<ExpediaAmenitiesProperty> expediaAmenitiesProperties = expediaAmenitiesPropertyDao.selectAll();
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
        Map<Long, ExpediaThemes> themesMap = expediaThemes.stream().collect(Collectors.toMap(ExpediaThemes::getId, Function.identity()));

        List<ExpediaContent> expediaContents = Lists.newArrayListWithCapacity(256);
        int page = 0;
        Integer load;
        String nextPageUrl = null;
        // 629479
        do {
            StopWatch watch = new StopWatch();
            watch.start();
            if ("".equals(nextPageUrl)) break;
            ExpediaResponse response = httpUtils.pullContent(nextPageUrl);
            watch.stop();
            log.info("获取第{}页内容, cost[s]: {},请求路径:{}", page, watch.getTotalTimeSeconds(), nextPageUrl);
            String body = response.getBody();
            load = response.getLoad();
            nextPageUrl = response.getNextPageUrl();
            try {
                transferBody2(expediaContents, body, amenitiesPropertyMap, amenitiesRoomsMap, amenitiesRatesMap,
                        attributesPetsMap, attributesGeneralMap, themesMap, imagesMap, categoriesMap, statisticsMap,
                        spokenLanguagesMap, roomViewsMap);
            } catch (Exception e) {
                log.error("转换对象异常:{}", Throwables.getStackTraceAsString(e));
                throw new Exception("当前第" + page + "页，请求路径：" + nextPageUrl + "转换数据出错");
            }
            expediaContentDao.saveBatch(expediaContents);
            expediaContents.clear();
            page++;
        } while (load > 0);
    }

    private void transferBody2(List<ExpediaContent> expediaContents, String body,
                               Map<Long, ExpediaAmenitiesProperty> amenitiesPropertyMap,
                               Map<Long, ExpediaAmenitiesRooms> amenitiesRoomsMap,
                               Map<Long, ExpediaAmenitiesRates> amenitiesRatesMap,
                               Map<Long, ExpediaAttributesPets> attributesPetsMap,
                               Map<Long, ExpediaAttributesGeneral> attributesGeneralMap,
                               Map<Long, ExpediaThemes> themesMap,
                               Map<Long, ExpediaImages> imagesMap,
                               Map<Long, ExpediaCategories> categoriesMap,
                               Map<Long, ExpediaStatistics> statisticsMap,
                               Map<String, ExpediaSpokenLanguages> spokenLanguagesMap,
                               Map<Long, ExpediaRoomViews> roomViewsMap) {
        JSONObject jsonObject = JSON.parseObject(body);
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
            if (ratings != null && ratings.getProperty() != null) {
                expediaContent.setStarRating(new BigDecimal(ratings.getProperty().getRating()));
            }
            Location location = content.getLocation();
            expediaContent.setLongitude(location.getCoordinates().getLongitude());
            expediaContent.setLatitude(location.getCoordinates().getLatitude());
            expediaContent.setTelephone(content.getPhone());
            expediaContent.setFax(content.getFax());
            String category = content.getCategory();
            expediaContent.setCategory(JSON.parseObject(category).get("name").toString());
            expediaContent.setRank((long) content.getRank());
            Checkin checkin = content.getCheckin();
            expediaContent.setCheckInAllDay(checkin.get_24hour());
            expediaContent.setCheckInStartTime(checkin.getBegin_time());
            expediaContent.setCheckInEndTime(checkin.getEnd_time());
            expediaContent.setCheckInInstructions(checkin.getInstructions());
            expediaContent.setCheckInSpecialInstructions(checkin.getSpecial_instructions());
            expediaContent.setCheckInMinAge(checkin.getMin_age());
            Checkout checkout = content.getCheckout();
            expediaContent.setCheckOutTime(checkout.getTime());
            expediaContent.setFees(JSON.toJSONString(content.getFees()));
            Policies policies = content.getPolicies();
            expediaContent.setKnowBeforeYouGo(policies == null ? null : policies.getKnow_before_you_go());
            expediaContent.setAttributes(JSON.toJSONString(content.getAttributes()));
            expediaContent.setAmenities(content.getAmenities());
            List<Images> images = content.getImages();
            expediaContent.setImages(JSON.toJSONString(images));
            Map<String, Room> rooms = content.getRooms();
            if (rooms != null) {
                expediaContent.setRooms(JSON.toJSONString(rooms.values()));
            }
            expediaContent.setRates(content.getRates());
            HotelDates dates = content.getDates();
            expediaContent.setAddedTime(dates.getAdded());
            expediaContent.setUpdatedTime(dates.getUpdated());
            Descriptions descriptions = content.getDescriptions();
            expediaContent.setDescriptions(JSON.toJSONString(descriptions));
            expediaContent.setStatistics(content.getStatistics());
            String airports = content.getAirports();
            expediaContent.setAirports(airports);
            expediaContent.setThemes(content.getThemes());
            expediaContent.setAllInclusive(JSON.toJSONString(content.getAll_inclusive()));
            expediaContent.setTaxId(content.getTax_id());
            expediaContent.setChain(content.getChain());
            expediaContent.setBrand(content.getBrand());
            expediaContent.setSpokenLanguages(content.getSpoken_languages());
            expediaContent.setCreateTime(new Date());
            expediaContents.add(expediaContent);
        }
    }
}
