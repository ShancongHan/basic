package com.example.basic.service;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.*;
import com.example.basic.domain.to.Coordinates;
import com.example.basic.entity.*;
import com.example.basic.utils.HttpUtils;
import com.example.basic.utils.IOUtils;
import com.example.basic.utils.TimeUtils;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author han
 * {@code @date} 2024/12/19
 */
@Slf4j
@Service
public class WstGlobalService {

    @Resource
    private WstHotelGlobalCategoryDictionaryDao wstHotelGlobalCategoryDictionaryDao;
    @Resource
    private WstHotelGlobalThemesDictionaryDao wstHotelGlobalThemesDictionaryDao;
    @Resource
    private WstHotelGlobalStatisticsDictionaryDao wstHotelGlobalStatisticsDictionaryDao;
    @Resource
    private WstHotelGlobalImagesDictionaryDao wstHotelGlobalImagesDictionaryDao;
    @Resource
    private WstHotelGlobalSpokenLanguagesDictionaryDao wstHotelGlobalSpokenLanguagesDictionaryDao;
    @Resource
    private WstHotelGlobalRoomViewsDictionaryDao wstHotelGlobalRoomViewsDictionaryDao;
    @Resource
    private WstHotelGlobalAmenitiesRoomsDictionaryDao wstHotelGlobalAmenitiesRoomsDictionaryDao;
    @Resource
    private WstHotelGlobalAmenitiesPropertyDictionaryDao wstHotelGlobalAmenitiesPropertyDictionaryDao;
    @Resource
    private WstHotelGlobalOnsitePaymentDictionaryDao wstHotelGlobalOnsitePaymentDictionaryDao;
    @Resource
    private WstHotelGlobalAmenitiesRatesDictionaryDao wstHotelGlobalAmenitiesRatesDictionaryDao;
    @Resource
    private WstHotelGlobalPetsDictionaryDao wstHotelGlobalPetsDictionaryDao;
    @Resource
    private WstHotelGlobalGeneralDictionaryDao wstHotelGlobalGeneralDictionaryDao;

    @Resource
    private WstHotelGlobalBrandDao wstHotelGlobalBrandDao;
    @Resource
    private WstHotelGlobalChainDao wstHotelGlobalChainDao;
    @Resource
    private HttpUtils httpUtils;
    @Resource
    private ExpediaInfoDao expediaInfoDao;
    @Resource
    private ExpediaPolicyDao expediaPolicyDao;
    @Resource
    private ExpediaAttributesDao expediaAttributesDao;
    @Resource
    private ExpediaAmenitiesDao expediaAmenitiesDao;
    @Resource
    private ExpediaImagesDao expediaImagesDao;
    @Resource
    private ExpediaImportantInfoDao expediaImportantInfoDao;
    @Resource
    private ExpediaRoomsDao expediaRoomsDao;
    @Resource
    private ExpediaRoomsAmenitiesDao expediaRoomsAmenitiesDao;
    @Resource
    private ExpediaRoomsImagesDao expediaRoomsImagesDao;
    @Resource
    private ExpediaStatisticsDao expediaStatisticsDao;
    @Resource
    private ExpediaDetailInfoDao expediaDetailInfoDao;

    public void categoryDictionary() throws Exception {
        //initCategory();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\categories-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split("-");
            WstHotelGlobalCategoryDictionary dictionary = new WstHotelGlobalCategoryDictionary();
            dictionary.setNameEn(split[0]);
            dictionary.setName(split[1]);
            wstHotelGlobalCategoryDictionaryDao.updateByName(dictionary);
        }
    }

    private void initCategory() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\categories.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalCategoryDictionary> list = parseCategory(json);
        wstHotelGlobalCategoryDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalCategoryDictionary> parseCategory(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject categories = (JSONObject) jsonObject.get("categories");
        Collection<Object> values = categories.values();
        List<WstHotelGlobalCategoryDictionary> dictionaries = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            WstHotelGlobalCategoryDictionary dictionary = new WstHotelGlobalCategoryDictionary();
            dictionary.setId(Integer.valueOf(id));
            dictionary.setNameEn(nameEn);
            dictionary.setSysCreateTime(date);
            dictionary.setExpediaFileTime(expediaFileTime);
            dictionaries.add(dictionary);
        }
        return dictionaries;
    }

    public void themesDictionary() throws Exception{
        //initThemes();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\themes-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalThemesDictionary update = new WstHotelGlobalThemesDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalThemesDictionaryDao.updateByName(update);
        }
    }

    private void initThemes() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\themes.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalThemesDictionary> list = parseThemes(json);
        wstHotelGlobalThemesDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalThemesDictionary> parseThemes(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject categories = (JSONObject) jsonObject.get("themes");
        Collection<Object> values = categories.values();
        List<WstHotelGlobalThemesDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            WstHotelGlobalThemesDictionary one = new WstHotelGlobalThemesDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void statisticsDictionary() throws Exception{
        //initStatistics();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\statistics-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalStatisticsDictionary update = new WstHotelGlobalStatisticsDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalStatisticsDictionaryDao.updateByName(update);
        }
    }

    private void initStatistics() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\statistics.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalStatisticsDictionary> list = parseStatistics(json);
        wstHotelGlobalStatisticsDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalStatisticsDictionary> parseStatistics(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject categories = (JSONObject) jsonObject.get("statistics");
        Collection<Object> values = categories.values();
        List<WstHotelGlobalStatisticsDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            WstHotelGlobalStatisticsDictionary one = new WstHotelGlobalStatisticsDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void imagesDictionary() throws Exception{
//        initImages();
        updateImageName();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\images_group-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalImagesDictionary update = new WstHotelGlobalImagesDictionary();
            update.setGroupEn(split[0]);
            update.setGroup(split[1]);
            wstHotelGlobalImagesDictionaryDao.updateGroupName(update);
        }
    }

    private void updateImageName() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\images-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalImagesDictionary update = new WstHotelGlobalImagesDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalImagesDictionaryDao.updateByName(update);
        }
    }

    private void initImages() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\images.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalImagesDictionary> list = parseImages(json);
        wstHotelGlobalImagesDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalImagesDictionary> parseImages(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject categories = (JSONObject) jsonObject.get("images");
        Collection<Object> values = categories.values();
        List<WstHotelGlobalImagesDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name") == null ? null : valueObject.get("name").toString();
            WstHotelGlobalImagesDictionary one = new WstHotelGlobalImagesDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void spokenLanguagesDictionary() throws Exception{
        //spokenLanguagesImages();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\spoken_languages-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalSpokenLanguagesDictionary update = new WstHotelGlobalSpokenLanguagesDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalSpokenLanguagesDictionaryDao.updateByName(update);
        }
    }

    private void spokenLanguagesImages() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\spoken_languages.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalSpokenLanguagesDictionary> list = parseSpokenLanguages(json);
        wstHotelGlobalSpokenLanguagesDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalSpokenLanguagesDictionary> parseSpokenLanguages(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject categories = (JSONObject) jsonObject.get("spoken_languages");
        Collection<Object> values = categories.values();
        List<WstHotelGlobalSpokenLanguagesDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            WstHotelGlobalSpokenLanguagesDictionary one = new WstHotelGlobalSpokenLanguagesDictionary();
            one.setId(id);
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void roomViewsDictionary() throws Exception{
        //initRoomViews();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\room_views-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalRoomViewsDictionary update = new WstHotelGlobalRoomViewsDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalRoomViewsDictionaryDao.updateByName(update);
        }
    }

    private void initRoomViews() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\room_views.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalRoomViewsDictionary> list = parseRoomViews(json);
        wstHotelGlobalRoomViewsDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalRoomViewsDictionary> parseRoomViews(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject categories = (JSONObject) jsonObject.get("room_views");
        Collection<Object> values = categories.values();
        List<WstHotelGlobalRoomViewsDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            WstHotelGlobalRoomViewsDictionary one = new WstHotelGlobalRoomViewsDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void amenitiesRoomsDictionary() throws Exception{
        //initAmenitiesRooms();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\amenities_rooms-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalAmenitiesRoomsDictionary update = new WstHotelGlobalAmenitiesRoomsDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalAmenitiesRoomsDictionaryDao.updateByName(update);
        }
    }
    private void initAmenitiesRooms() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\amenities_rooms.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalAmenitiesRoomsDictionary> list = parseAmenitiesRooms(json);
        wstHotelGlobalAmenitiesRoomsDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalAmenitiesRoomsDictionary> parseAmenitiesRooms(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject object = (JSONObject) jsonObject.get("amenities_rooms");
        Collection<Object> values = object.values();
        List<WstHotelGlobalAmenitiesRoomsDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            WstHotelGlobalAmenitiesRoomsDictionary one = new WstHotelGlobalAmenitiesRoomsDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void amenitiesPropertyDictionary() throws Exception{
        //initAmenitiesProperty();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\amenities_property-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalAmenitiesPropertyDictionary update = new WstHotelGlobalAmenitiesPropertyDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalAmenitiesPropertyDictionaryDao.updateByName(update);
        }
    }

    private void initAmenitiesProperty() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\amenities_property.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalAmenitiesPropertyDictionary> list = parseAmenitiesProperty(json);
        wstHotelGlobalAmenitiesPropertyDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalAmenitiesPropertyDictionary> parseAmenitiesProperty(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject object = (JSONObject) jsonObject.get("amenities_property");
        Collection<Object> values = object.values();
        List<WstHotelGlobalAmenitiesPropertyDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            boolean hanValue = (boolean) valueObject.get("has_value");
            WstHotelGlobalAmenitiesPropertyDictionary one = new WstHotelGlobalAmenitiesPropertyDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setHasValue(hanValue);
            one.setSysCreateTime(date);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void onsitePaymentDictionary() throws Exception{
//        initOnsitePayment();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\onsite_payment-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalOnsitePaymentDictionary update = new WstHotelGlobalOnsitePaymentDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalOnsitePaymentDictionaryDao.updateByName(update);
        }
    }

    private void initOnsitePayment() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\onsite_payment.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalOnsitePaymentDictionary> list = parseOnsitePayment(json);
        wstHotelGlobalOnsitePaymentDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalOnsitePaymentDictionary> parseOnsitePayment(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject object = (JSONObject) jsonObject.get("onsite_payment_types");
        Collection<Object> values = object.values();
        List<WstHotelGlobalOnsitePaymentDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            WstHotelGlobalOnsitePaymentDictionary one = new WstHotelGlobalOnsitePaymentDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void amenitiesRatesDictionary() throws Exception{
//        initAmenitiesRates();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\amenities_rates-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalAmenitiesRatesDictionary update = new WstHotelGlobalAmenitiesRatesDictionary();
            update.setNameEn(split[0]);
            update.setName(split[1]);
            wstHotelGlobalAmenitiesRatesDictionaryDao.updateByName(update);
        }
    }

    private void initAmenitiesRates() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\amenities_rates.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalAmenitiesRatesDictionary> list = parseAmenitiesRates(json);
        wstHotelGlobalAmenitiesRatesDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalAmenitiesRatesDictionary> parseAmenitiesRates(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject object = (JSONObject) jsonObject.get("amenities_rates");
        Collection<Object> values = object.values();
        List<WstHotelGlobalAmenitiesRatesDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            boolean hanValue = (boolean) valueObject.get("has_value");
            WstHotelGlobalAmenitiesRatesDictionary one = new WstHotelGlobalAmenitiesRatesDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setHasValue(hanValue);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void petsDictionary() throws Exception{
//        initPets();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\attributes_pets-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalPetsDictionary dictionary = new WstHotelGlobalPetsDictionary();
            dictionary.setNameEn(split[0]);
            dictionary.setName(split[1]);
            wstHotelGlobalPetsDictionaryDao.updateByName(dictionary);
        }
    }

    private void initPets() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\attributes_pets.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalPetsDictionary> list = parsePets(json);
        wstHotelGlobalPetsDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalPetsDictionary> parsePets(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject object = (JSONObject) jsonObject.get("attributes_pets");
        Collection<Object> values = object.values();
        List<WstHotelGlobalPetsDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            boolean hanValue = (boolean) valueObject.get("has_value");
            WstHotelGlobalPetsDictionary one = new WstHotelGlobalPetsDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setHasValue(hanValue);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void generalDictionary() throws Exception{
//        initGeneral();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\attributes_general-翻译.txt";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        for (String line : lines) {
            String[] split = line.split(" - ");
            WstHotelGlobalGeneralDictionary dictionary = new WstHotelGlobalGeneralDictionary();
            dictionary.setNameEn(split[0]);
            dictionary.setName(split[1]);
            wstHotelGlobalGeneralDictionaryDao.updateByName(dictionary);
        }
    }

    private void initGeneral() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\attributes_general.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalGeneralDictionary> list = parseGeneral(json);
        wstHotelGlobalGeneralDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalGeneralDictionary> parseGeneral(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject object = (JSONObject) jsonObject.get("attributes_general");
        Collection<Object> values = object.values();
        List<WstHotelGlobalGeneralDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
            boolean hanValue = (boolean) valueObject.get("has_value");
            WstHotelGlobalGeneralDictionary one = new WstHotelGlobalGeneralDictionary();
            one.setId(Integer.valueOf(id));
            one.setNameEn(nameEn);
            one.setSysCreateTime(date);
            one.setHasValue(hanValue);
            one.setExpediaFileTime(expediaFileTime);
            list.add(one);
        }
        return list;
    }

    public void chainBrand() throws Exception{
        List<WstHotelGlobalChain> chains = Lists.newArrayList();
        List<WstHotelGlobalBrand> brands = Lists.newArrayList();
        String response = httpUtils.pullChain();
        try {
            transferChainAndBrand(chains, brands, response);
        } catch (Exception e) {
            log.error("解析发生异常:{}", Throwables.getStackTraceAsString(e));
        }
        wstHotelGlobalChainDao.saveBatch(chains);
        wstHotelGlobalBrandDao.saveBatch(brands);
    }

    private void transferChainAndBrand(List<WstHotelGlobalChain> chains, List<WstHotelGlobalBrand> brands, String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        Date date = new Date();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            WstHotelGlobalChain chain = new WstHotelGlobalChain();
            JSONObject value = (JSONObject) entry.getValue();
            Integer chainId = Integer.valueOf(value.get("id").toString());
            chain.setId(chainId);
            chain.setNameEn((String) value.get("name"));
            chain.setUpdateTime(date);
            chains.add(chain);

            JSONObject object = (JSONObject) value.get("brands");
            if (object != null) {
                for (Map.Entry<String, Object> entry1 : object.entrySet()) {
                    JSONObject value1 = (JSONObject) entry1.getValue();
                    WstHotelGlobalBrand brand = new WstHotelGlobalBrand();
                    brand.setId(Integer.valueOf(value1.get("id").toString()));
                    brand.setNameEn((String) value1.get("name"));
                    brand.setChainId(chainId);
                    brand.setUpdateTime(date);
                    brands.add(brand);
                }
            }
        }
    }

    public void analyzeHotelFile() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\20241225\\en-US.expediacollect.propertycatalog.jsonl";
        ImmutableList<String> lines = Files.asCharSource(new File(fileName), Charset.defaultCharset()).readLines();
        List<ExpediaInfo> expediaInfos = Lists.newArrayListWithCapacity(1000);
        Date now = new Date();
        for (String line : lines) {
            ExpediaInfo expediaInfo = parse(line, now);
            expediaInfos.add(expediaInfo);
            if (expediaInfos.size() == 1000) {
                expediaInfoDao.saveBatch(expediaInfos);
                expediaInfos.clear();
            }
        }
        if (expediaInfos.size() > 0) {
            expediaInfoDao.saveBatch(expediaInfos);
        }
    }

    private ExpediaInfo parse(String line, Date now) throws Exception {
        JSONObject jsonObject = JSON.parseObject(line);
        ExpediaInfo info = new ExpediaInfo();
        info.setHotelId((String) jsonObject.get("property_id"));
        info.setNameEn((String) jsonObject.get("name"));
        if (info.getNameEn().length() > 250) {
            System.out.println(info.getHotelId());
        }
        JSONObject address = (JSONObject) jsonObject.get("address");
        if (address != null) {
            String line1 = (String) address.get("line_1");
            String line2 = (String) address.get("line_2");
            info.setAddressEn(line1 + (StringUtils.hasLength(line2) ? line2 : ""));
            info.setCountryCode((String) address.get("country_code"));
            info.setProvinceCode((String) address.get("state_province_code"));
            info.setProvinceNameEn((String) address.get("state_province_name"));
            info.setCityEn((String) address.get("city"));
            info.setZipCode((String) address.get("postal_code"));
        }

        JSONObject location = (JSONObject) jsonObject.get("location");
        if (location != null) {
            Object object = location.get("coordinates");
            if (StringUtils.hasLength(object.toString())) {
                Coordinates coordinates1 = JSON.parseObject(object.toString(), Coordinates.class);
                info.setLongitude(coordinates1.getLongitude());
                info.setLatitude(coordinates1.getLatitude());
            }
        }

        info.setTelephone((String) jsonObject.get("phone"));

        JSONObject category = (JSONObject) jsonObject.get("category");
        if (category != null) {
            info.setCategoryId(Integer.valueOf((String) category.get("id")));
            info.setCategoryEn((String) category.get("name"));
        }
        Integer rank = (Integer) jsonObject.get("rank");
        info.setRank(rank);

        JSONObject businessModel = (JSONObject) jsonObject.get("business_model");
        if (businessModel != null) {
            info.setExpediaCollect((Boolean) businessModel.get("expedia_collect"));
            info.setPropertyCollect((Boolean) businessModel.get("property_collect"));
        }

        JSONObject chain = (JSONObject) jsonObject.get("chain");
        if (chain != null) {
            info.setChainId(Integer.valueOf((String) chain.get("id")));
            info.setChainNameEn((String) chain.get("name"));
        }

        JSONObject brand = (JSONObject) jsonObject.get("brand");
        if (brand != null) {
            info.setBrandId(Integer.valueOf((String) brand.get("id")));
            info.setBrandNameEn((String) brand.get("name"));
        }
        info.setSupplySource((String) jsonObject.get("supply_source"));

        JSONObject dates = (JSONObject) jsonObject.get("dates");
        if (dates != null) {
            info.setAddedTime(transferDateTime((String) dates.get("added")));
            info.setUpdatedTime(transferDateTime((String) dates.get("updated")));
        }
        info.setCreateTime(now);
        return info;
    }

    // 2023-11-23T09:05:37.357Z
    private Date transferDateTime(String dateTime) throws Exception {
        if (!StringUtils.hasLength(dateTime)) return null;
        dateTime = dateTime.replace("T", " ");
        dateTime = dateTime.substring(0, dateTime.indexOf("."));
        return DateUtils.parseDate(dateTime);
    }

    public void pullHotelEn() throws Exception {
        List<String> propertyIds = expediaInfoDao.selectNeedUpdate();
        int max = Math.max(250, propertyIds.size());
        for (int i = 0; i < max; ) {
            int start = i;
            int end = Math.min(i + 250, max);
            List<String> queryList = propertyIds.subList(start, end);
            String body = httpUtils.pullHotelDetailListByIdsEn(queryList);
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
}
