package com.example.basic.service;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.*;
import com.example.basic.domain.expedia.Pets;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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

    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private TransactionDefinition transactionDefinition;
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
        /*if (info.getNameEn().length() > 250) {
            System.out.println(info.getHotelId());
        }*/
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
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("查询db");
        List<String> propertyIds = expediaInfoDao.selectNeedUpdate();
        stopWatch.stop();
        log.info("查询db花费时间: {}", stopWatch.getTotalTimeMillis());
        int max = Math.max(250, propertyIds.size());
        Date date = new Date();
        List<ExpediaInfo> expediaInfos = Lists.newArrayListWithCapacity(250);
        List<ExpediaDetailInfo> expediaDetailInfos = Lists.newArrayListWithCapacity(250);
        List<ExpediaImages> expediaImages = Lists.newArrayListWithCapacity(20000);
        List<ExpediaPolicy> expediaPolicies = Lists.newArrayListWithCapacity(250);
        List<ExpediaRooms> expediaRooms = Lists.newArrayListWithCapacity(5000);
        List<ExpediaRoomsImages> expediaRoomsImages = Lists.newArrayListWithCapacity(60000);
        List<ExpediaRoomsAmenities> expediaRoomsAmenities = Lists.newArrayListWithCapacity(80000);
        List<ExpediaAmenities> expediaAmenities = Lists.newArrayListWithCapacity(5000);
        List<ExpediaAttributes> expediaAttributes = Lists.newArrayListWithCapacity(6000);
        List<ExpediaImportantInfo> expediaImportantInfos = Lists.newArrayListWithCapacity(250);
        List<ExpediaStatistics> expediaStatistics = Lists.newArrayListWithCapacity(1000);
        for (int i = 0; i < max; ) {
            StopWatch watch = new StopWatch();
            int start = i;
            int end = Math.min(i + 250, max);
            List<String> queryList = propertyIds.subList(start, end);
            watch.start("请求酒店");
            String body = null;
            int retry = 0;
            int maxRetry = 10;
            while (retry <= maxRetry) {
                try {
                    body = httpUtils.pullHotelDetailListByIdsEn(queryList);
                    if (StringUtils.hasLength(body)) break;
                } catch (TimeoutException | SocketException exception) {
                    log.error("本次超时");
                    retry++;
                }
            }
            watch.stop();
            i += 250;
            JSONObject jsonObject = JSON.parseObject(body);
            watch.start("转换并存库");
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String hotelId = entry.getKey();
                JSONObject hotelDetail = (JSONObject) entry.getValue();
                convertDetail(hotelId, hotelDetail, expediaInfos, expediaDetailInfos, expediaImages, expediaPolicies, expediaRooms,
                        expediaRoomsImages, expediaRoomsAmenities, expediaAmenities, expediaAttributes, expediaImportantInfos,
                        expediaStatistics, date);
                TransactionStatus transaction = transactionManager.getTransaction(transactionDefinition);
                try {
                    saveOrUpdate(expediaInfos, expediaDetailInfos, expediaImages, expediaPolicies, expediaRooms,
                            expediaRoomsImages, expediaRoomsAmenities, expediaAmenities, expediaAttributes, expediaImportantInfos, expediaStatistics);
                    transactionManager.commit(transaction);
                } catch (Exception exception) {
                    log.info("本此批次抛出异常，这些id:{}跳过", queryList);
                    log.error("本此批次抛出异常{}，跳过", Throwables.getStackTraceAsString(exception));
                    transactionManager.rollback(transaction);
                }
                expediaInfos.clear();
                expediaDetailInfos.clear();
                expediaImages.clear();
                expediaPolicies.clear();
                expediaRooms.clear();
                expediaRoomsImages.clear();
                expediaRoomsAmenities.clear();
                expediaAmenities.clear();
                expediaAttributes.clear();
                expediaImportantInfos.clear();
                expediaStatistics.clear();
            }
            watch.stop();
            log.info("本批次执行总时长：{}, 具体耗时为：{}", watch.getTotalTimeMillis(), watch.prettyPrint());
        }
    }

    public void saveOrUpdate(List<ExpediaInfo> expediaInfos, List<ExpediaDetailInfo> expediaDetailInfos,
                             List<ExpediaImages> expediaImages, List<ExpediaPolicy> expediaPolicies,
                             List<ExpediaRooms> expediaRooms, List<ExpediaRoomsImages> expediaRoomsImages,
                             List<ExpediaRoomsAmenities> expediaRoomsAmenities, List<ExpediaAmenities> expediaAmenities,
                             List<ExpediaAttributes> expediaAttributes, List<ExpediaImportantInfo> expediaImportantInfos,
                             List<ExpediaStatistics> expediaStatistics) {
        if (!CollectionUtils.isEmpty(expediaInfos)) {
            for (ExpediaInfo expediaInfo : expediaInfos) {
                expediaInfoDao.update(expediaInfo);
            }
        }
        if (!CollectionUtils.isEmpty(expediaDetailInfos)) {
            expediaDetailInfoDao.saveBatch(expediaDetailInfos);
        }
        if (!CollectionUtils.isEmpty(expediaImages)) {
            expediaImagesDao.saveBatch(expediaImages);
        }
        if (!CollectionUtils.isEmpty(expediaPolicies)) {
            expediaPolicyDao.saveBatch(expediaPolicies);
        }
        if (!CollectionUtils.isEmpty(expediaRooms)) {
            expediaRoomsDao.saveBatch(expediaRooms);
        }
        if (!CollectionUtils.isEmpty(expediaRoomsImages)) {
            expediaRoomsImagesDao.saveBatch(expediaRoomsImages);
        }
        if (!CollectionUtils.isEmpty(expediaRoomsAmenities)) {
            expediaRoomsAmenitiesDao.saveBatch(expediaRoomsAmenities);
        }
        if (!CollectionUtils.isEmpty(expediaAmenities)) {
            expediaAmenitiesDao.saveBatch(expediaAmenities);
        }
        if (!CollectionUtils.isEmpty(expediaAttributes)) {
            expediaAttributesDao.saveBatch(expediaAttributes);
        }
        if (!CollectionUtils.isEmpty(expediaImportantInfos)) {
            expediaImportantInfoDao.saveBatch(expediaImportantInfos);
        }
        if (!CollectionUtils.isEmpty(expediaStatistics)) {
            expediaStatisticsDao.saveBatch(expediaStatistics);
        }
    }

    private void convertDetail(String hotelId, JSONObject hotelDetail, List<ExpediaInfo> expediaInfos,
                               List<ExpediaDetailInfo> expediaDetailInfos, List<ExpediaImages> expediaImages,
                               List<ExpediaPolicy> expediaPolicies, List<ExpediaRooms> expediaRooms,
                               List<ExpediaRoomsImages> expediaRoomsImages, List<ExpediaRoomsAmenities> expediaRoomsAmenities,
                               List<ExpediaAmenities> expediaAmenities, List<ExpediaAttributes> expediaAttributes,
                               List<ExpediaImportantInfo> expediaImportantInfos, List<ExpediaStatistics> expediaStatistics,
                               Date date) throws Exception{
        ExpediaInfo expediaInfo = new ExpediaInfo();
        expediaInfo.setHotelId(hotelId);
        expediaInfo.setNameEn(getStringValueByKey(hotelDetail, "name"));
        JSONObject address = (JSONObject) hotelDetail.get("address");
        if (address != null) {
            String line1 = getStringValueByKey(address, "line_1");
            String line2 = getStringValueByKey(address, "line_2");
            expediaInfo.setAddressEn(line1 + (StringUtils.hasLength(line2) ? line2 : ""));
            expediaInfo.setCountryCode(getStringValueByKey(address, "country_code"));
            expediaInfo.setProvinceCode(getStringValueByKey(address, "state_province_code"));
            expediaInfo.setProvinceNameEn(getStringValueByKey(address, "state_province_name"));
            expediaInfo.setCityEn(getStringValueByKey(address, "city"));
            expediaInfo.setZipCode(getStringValueByKey(address, "postal_code"));
        }
        JSONObject ratings = (JSONObject) hotelDetail.get("ratings");
        if (ratings != null) {
            JSONObject property = (JSONObject) ratings.get("property");
            if (property != null) {
                expediaInfo.setStarRating(getBigDecimalValueByKey(property, "rating"));
            }
            // TODO 看下是否要处理
            String guest = getStringValueByKey(ratings, "guest");
            expediaInfo.setGuest(guest);
            if (guest != null) {
                JSONObject guestJson = (JSONObject) ratings.get("guest");
                expediaInfo.setScore(getBigDecimalValueByKey(guestJson, "overall"));
            }
        }

        JSONObject location = (JSONObject) hotelDetail.get("location");
        if (location != null) {
            String coordinates = getStringValueByKey(location, "coordinates");
            if (StringUtils.hasLength(coordinates)) {
                Coordinates coordinates1 = JSON.parseObject(coordinates, Coordinates.class);
                expediaInfo.setLongitude(coordinates1.getLongitude());
                expediaInfo.setLatitude(coordinates1.getLatitude());
            }
        }
        expediaInfo.setTelephone(getStringValueByKey(hotelDetail, "phone"));
        expediaInfo.setFax(getStringValueByKey(hotelDetail, "fax"));

        JSONObject category = (JSONObject) hotelDetail.get("category");
        if (category != null) {
            expediaInfo.setCategoryId(getIntegerValueByKey(category, "id"));
            expediaInfo.setCategoryEn(getStringValueByKey(category, "name"));
        }
        expediaInfo.setRank(getIntegerValueByKey(hotelDetail, "rank"));

        JSONObject businessModel = (JSONObject) hotelDetail.get("business_model");
        if (businessModel != null) {
            expediaInfo.setExpediaCollect(getBooleanValueByKey(businessModel, "expedia_collect"));
            expediaInfo.setPropertyCollect(getBooleanValueByKey(businessModel, "property_collect"));
        }
        ExpediaPolicy expediaPolicy = new ExpediaPolicy();
        expediaPolicy.setHotelId(hotelId);

        ExpediaImportantInfo expediaImportantInfo = new ExpediaImportantInfo();
        expediaImportantInfo.setHotelId(hotelId);
        JSONObject checkin = (JSONObject) hotelDetail.get("checkin");
        if (checkin != null) {
            expediaPolicy.setAllDayCheckin(getStringValueByKey(checkin,"24_hour"));
            expediaPolicy.setCheckinStart(getStringValueByKey(checkin,"begin_time"));
            expediaPolicy.setCheckinEnd(getStringValueByKey(checkin,"end_time"));
            expediaPolicy.setMinAge(getIntegerValueByKey(checkin,"min_age"));
            expediaPolicy.setSpecialInstructionsEn(getStringValueByKey(checkin,"special_instructions"));
            /*if (StringUtils.hasLength(expediaPolicy.getSpecialInstructionsEn()) && expediaPolicy.getSpecialInstructionsEn().length() > 1000) {
                System.out.println(expediaPolicy.getSpecialInstructionsEn());
            }*/
            expediaImportantInfo.setInstructionsEn(getStringValueByKey(checkin, "instructions"));
        }
        JSONObject checkout = (JSONObject) hotelDetail.get("checkout");
        if (checkout != null) {
            expediaPolicy.setCheckoutTime(getStringValueByKey(checkout,"time"));
        }
        JSONObject fees = (JSONObject) hotelDetail.get("fees");
        if (fees != null) {
            expediaImportantInfo.setFeeMandatory(getStringValueByKey(fees,"mandatory"));
            expediaImportantInfo.setFeeOptional(getStringValueByKey(fees,"optional"));
        }
        JSONObject policies = (JSONObject) hotelDetail.get("policies");
        if (policies != null) {
            expediaImportantInfo.setKnowBeforeYouGoEn(getStringValueByKey(policies,"know_before_you_go"));
        }
        expediaImportantInfos.add(expediaImportantInfo);
        JSONObject attributes = (JSONObject) hotelDetail.get("attributes");
        if (attributes != null) {
            JSONObject general = (JSONObject) attributes.get("general");
            if (general != null) {
                for (Map.Entry<String, Object> entry : general.entrySet()) {
                    String generalId = entry.getKey();
                    JSONObject realGeneral = (JSONObject) entry.getValue();
                    ExpediaAttributes expediaAttribute = new ExpediaAttributes();
                    expediaAttribute.setHotelId(hotelId);
                    expediaAttribute.setGeneralId(Integer.valueOf(generalId));
                    expediaAttribute.setGeneralNameEn(getStringValueByKey(realGeneral, "name"));
                    /*if (StringUtils.hasLength(expediaAttribute.getGeneralNameEn()) && expediaAttribute.getGeneralNameEn().length() > 100) {
                        System.out.println(expediaAttribute.getGeneralNameEn());
                    }*/
                    expediaAttribute.setGeneralValue(getStringValueByKey(realGeneral, "value"));
                    expediaAttributes.add(expediaAttribute);
                }
            }
            JSONObject pets = (JSONObject) attributes.get("pets");
            if (pets != null) {
                List<Pets> petList = Lists.newArrayListWithCapacity(10);
                for (Map.Entry<String, Object> entry : pets.entrySet()) {
                    Pets pet = JSON.parseObject(entry.getValue().toString(), Pets.class);
                    petList.add(pet);
                }
                expediaPolicy.setPetsEn(JSON.toJSONString(petList));
                /*if (StringUtils.hasLength(expediaPolicy.getPetsEn()) && expediaPolicy.getPetsEn().length() > 800) {
                    System.out.println(expediaPolicy.getPetsEn());
                }*/
            }
        }
        JSONObject amenities = (JSONObject) hotelDetail.get("amenities");
        if (amenities != null) {
            for (Map.Entry<String, Object> entry : amenities.entrySet()) {
                String amenitiesId = entry.getKey();
                JSONObject realAmenities = (JSONObject) entry.getValue();
                ExpediaAmenities one = new ExpediaAmenities();
                one.setHotelId(hotelId);
                one.setAmenitiesId(Integer.valueOf(amenitiesId));
                one.setAmenitiesNameEn(getStringValueByKey(realAmenities, "name"));
                one.setAmenitiesValue(getStringValueByKey(realAmenities, "value"));
                JSONArray categoriesArray = (JSONArray) realAmenities.get("categories");
                if (categoriesArray != null && categoriesArray.size() > 0) {
                    one.setAmenitiesCategories(categoriesArray.stream().map(Object::toString).collect(Collectors.joining(",")));
                }
                expediaAmenities.add(one);
            }
        }
        JSONArray images = (JSONArray) hotelDetail.get("images");
        if (images != null && images.size() > 0) {
            for (Object image : images) {
                JSONObject imageJson = (JSONObject) image;
                ExpediaImages expediaImage = new ExpediaImages();
                expediaImage.setHotelId(hotelId);
                Boolean heroImage = getBooleanValueByKey(imageJson, "hero_image");
                expediaImage.setHeroImage(heroImage);
                expediaImage.setCategoryId(getIntegerValueByKey(imageJson, "category"));
                expediaImage.setCategoryEn(getStringValueByKey(imageJson, "caption"));
                JSONObject links = (JSONObject) imageJson.get("links");
                if (links != null) {
                    for (Map.Entry<String, Object> entry : links.entrySet()) {
                        String key = entry.getKey();
                        JSONObject link = (JSONObject) entry.getValue();
                        String href = getStringValueByKey(link, "href");
                        if ("350px".equals(key)) {
                            expediaImage.setSmallSizeUrl(href);
                            if (heroImage != null && heroImage) {
                                expediaInfo.setHeroImage(href);
                            }
                        }
                        if ("1000px".equals(key)) {
                            expediaImage.setLargeSizeUrl(href);
                        }
                    }
                }
                expediaImages.add(expediaImage);
            }
        }

        JSONObject onsitePayments = (JSONObject) hotelDetail.get("onsite_payments");
        if (onsitePayments != null) {
            expediaPolicy.setOnsitePaymentsCurrency(getStringValueByKey(onsitePayments, "currency"));
            JSONObject types = (JSONObject) onsitePayments.get("types");
            if (types != null) {
                expediaPolicy.setOnsitePayments(String.join(",", types.keySet()));
            }
            expediaPolicies.add(expediaPolicy);
        }

        JSONObject rooms = (JSONObject) hotelDetail.get("rooms");
        if (rooms != null) {
            for (Map.Entry<String, Object> entry : rooms.entrySet()) {
                String roomId = entry.getKey();
                JSONObject room = (JSONObject) entry.getValue();
                ExpediaRooms expediaRoom = new ExpediaRooms();
                expediaRoom.setHotelId(hotelId);
                expediaRoom.setRoomId(roomId);
                expediaRoom.setNameEn(getStringValueByKey(room, "name"));
                JSONObject descriptions = (JSONObject) room.get("descriptions");
                if (descriptions != null) {
                    expediaRoom.setDescriptionsEn(getStringValueByKey(descriptions, "overview"));
                }
                expediaRoom.setBedGroups(getStringValueByKey(room, "bed_groups"));
                JSONObject area = (JSONObject) room.get("area");
                if (area != null) {
                    expediaRoom.setAreaSquareFeet(getBigDecimalValueByKey(area, "square_feet"));
                    expediaRoom.setAreaSquareMeters(getBigDecimalValueByKey(area, "square_meters"));
                }
                JSONObject views = (JSONObject) room.get("views");
                if (views != null) {
                    expediaRoom.setRoomView(String.join(",",views.keySet()));
                }
                JSONObject occupancy = (JSONObject) room.get("occupancy");
                if (occupancy != null) {
                    JSONObject maxAllowed = (JSONObject) occupancy.get("max_allowed");
                    if (maxAllowed != null) {
                        expediaRoom.setMaxAllowedTotal(getIntegerValueByKey(maxAllowed, "total"));
                        if (expediaRoom.getMaxAllowedTotal() != null && expediaRoom.getMaxAllowedTotal() > 10) {
                            System.out.println(expediaRoom.getMaxAllowedTotal());
                        }
                        expediaRoom.setMaxAllowedAdults(getIntegerValueByKey(maxAllowed, "adults"));
                        expediaRoom.setMaxAllowedChildren(getIntegerValueByKey(maxAllowed, "children"));
                    }
                    JSONObject ageCategories = (JSONObject) occupancy.get("age_categories");
                    if (ageCategories != null) {
                        StringBuilder categoryStr = new StringBuilder();
                        StringBuilder age = new StringBuilder();
                        for (Map.Entry<String, Object>  ageCategoriesMap : ageCategories.entrySet()) {
                            JSONObject ageCategory = (JSONObject) ageCategoriesMap.getValue();
                            categoryStr.append(getStringValueByKey(ageCategory, "name")).append(",");
                            age.append(getStringValueByKey(ageCategory, "minimum_age")).append(",");
                        }
                        expediaRoom.setAgeCategoriesName(categoryStr.substring(0, categoryStr.length() -1));
                        expediaRoom.setAgeCategoriesMinimumAge(age.substring(0, age.length() -1));
                    }
                }
                expediaRooms.add(expediaRoom);

                JSONObject roomAmenities = (JSONObject) room.get("amenities");
                if (roomAmenities != null) {
                    for (Map.Entry<String, Object> roomAmenitiesMap : roomAmenities.entrySet()) {
                        JSONObject roomAmenitiesJson = (JSONObject) roomAmenitiesMap.getValue();
                        ExpediaRoomsAmenities expediaRoomsAmenities1 = new ExpediaRoomsAmenities();
                        expediaRoomsAmenities1.setHotelId(hotelId);
                        expediaRoomsAmenities1.setRoomId(roomId);
                        expediaRoomsAmenities1.setAmenitiesId(getIntegerValueByKey(roomAmenitiesJson, "id"));
                        expediaRoomsAmenities1.setAmenitiesNameEn(getStringValueByKey(roomAmenitiesJson, "name"));
                        expediaRoomsAmenities1.setAmenitiesValue(getStringValueByKey(roomAmenitiesJson, "value"));
                        /*if (StringUtils.hasLength(expediaRoomsAmenities1.getAmenitiesValue())) {
                            if (expediaRoomsAmenities1.getAmenitiesValue().length() > 30) {
                                System.out.println(expediaRoomsAmenities1.getAmenitiesValue());
                            }
                        }*/
                        expediaRoomsAmenities1.setAmenitiesCategories(getStringValueByKey(roomAmenitiesJson, "categories"));
                        expediaRoomsAmenities.add(expediaRoomsAmenities1);
                    }
                }

                JSONArray roomImages = (JSONArray) room.get("images");
                if (roomImages != null && roomImages.size() > 0) {
                    for (Object roomImage : roomImages) {
                        JSONObject roomImageJson = (JSONObject) roomImage;
                        ExpediaRoomsImages expediaRoomsImages1 = new ExpediaRoomsImages();
                        expediaRoomsImages1.setHotelId(hotelId);
                        expediaRoomsImages1.setRoomId(roomId);
                        expediaRoomsImages1.setHeroImage(getBooleanValueByKey(roomImageJson, "hero_image"));
                        expediaRoomsImages1.setCategoryId(getIntegerValueByKey(roomImageJson, "category"));
                        expediaRoomsImages1.setCategoryEn(getStringValueByKey(roomImageJson, "caption"));
                        JSONObject links = (JSONObject) roomImageJson.get("links");
                        if (links != null) {
                            for (Map.Entry<String, Object> entry1 : links.entrySet()) {
                                String key = entry1.getKey();
                                JSONObject link = (JSONObject) entry1.getValue();
                                String href = getStringValueByKey(link, "href");
                                if ("350px".equals(key)) {
                                    expediaRoomsImages1.setSmallSizeUrl(href);
                                }
                                if ("1000px".equals(key)) {
                                    expediaRoomsImages1.setLargeSizeUrl(href);
                                }
                            }
                        }
                        expediaRoomsImages.add(expediaRoomsImages1);
                    }
                }
            }
        }
        ExpediaDetailInfo expediaDetailInfo = new ExpediaDetailInfo();
        expediaDetailInfo.setHotelId(hotelId);
        // 此字段目前没有作用且过长，丢弃
        /*JSONObject rates = (JSONObject) hotelDetail.get("rates");
        if (rates != null) {
            expediaDetailInfo.setRates(rates.toString());
        }*/

        JSONObject dates = (JSONObject) hotelDetail.get("dates");
        if (dates != null) {
            expediaInfo.setAddedTime(transferDateTime(getStringValueByKey(dates, "added")));
            expediaInfo.setUpdatedTime(transferDateTime(getStringValueByKey(dates, "updated")));
        }

        JSONObject descriptions = (JSONObject) hotelDetail.get("descriptions");
        if (descriptions != null) {
            expediaDetailInfo.setDescriptionsEn(descriptions.toString());
        }
        JSONObject statistics = (JSONObject) hotelDetail.get("statistics");
        if (statistics != null) {
            for (Map.Entry<String, Object> statisticsMap : statistics.entrySet()) {
                JSONObject statisticsJson = (JSONObject) statisticsMap.getValue();
                ExpediaStatistics expediaStatistics1 = new ExpediaStatistics();
                expediaStatistics1.setHotelId(hotelId);
                expediaStatistics1.setStatisticsId(getIntegerValueByKey(statisticsJson, "id"));
                expediaStatistics1.setStatisticsNameEn(getStringValueByKey(statisticsJson, "name"));
                expediaStatistics1.setStatisticsValue(getStringValueByKey(statisticsJson, "value"));
                expediaStatistics.add(expediaStatistics1);
            }
        }

        JSONObject airports = (JSONObject) hotelDetail.get("airports");
        if (airports != null) {
            JSONObject preferred = (JSONObject) airports.get("preferred");
            if (preferred != null) {
                expediaInfo.setAirportNearby(getStringValueByKey(preferred, "iata_airport_code"));
            }
        }

        JSONObject themes = (JSONObject) hotelDetail.get("themes");
        if (themes != null) {
            expediaInfo.setThemes(String.join(",", themes.keySet()));
        }

        JSONObject allInclusive = (JSONObject) hotelDetail.get("all_inclusive");
        if (allInclusive != null) {
            expediaDetailInfo.setAllInclusive(allInclusive.toString());
        }
        expediaDetailInfo.setTaxId(getStringValueByKey(hotelDetail,"tax_id"));

        JSONObject chain = (JSONObject) hotelDetail.get("chain");
        if (chain != null) {
            expediaInfo.setChainId(getIntegerValueByKey(chain, "id"));
            expediaInfo.setChainNameEn(getStringValueByKey(chain, "name"));
        }

        JSONObject brand = (JSONObject) hotelDetail.get("brand");
        if (brand != null) {
            expediaInfo.setBrandId(getIntegerValueByKey(brand, "id"));
            expediaInfo.setBrandNameEn(getStringValueByKey(brand, "name"));
        }
        JSONObject spokenLanguages = (JSONObject) hotelDetail.get("spoken_languages");
        if (spokenLanguages != null) {
            expediaDetailInfo.setSpokenLanguages(String.join(",", spokenLanguages.keySet()));
        }
        expediaInfo.setSupplySource(getStringValueByKey(hotelDetail, "supplySource"));
        expediaDetailInfo.setRegistryNumber(getStringValueByKey(hotelDetail, "registry_number"));
        expediaInfo.setUpdateTime(date);
        expediaInfos.add(expediaInfo);
        expediaDetailInfos.add(expediaDetailInfo);
    }

    private Boolean getBooleanValueByKey(JSONObject jsonObject, String key) {
        Object object = jsonObject.get(key);
        if (object == null) return null;
        return Boolean.valueOf(object.toString());
    }

    private Integer getIntegerValueByKey(JSONObject jsonObject, String key) {
        Object object = jsonObject.get(key);
        if (object == null) return null;
        return Integer.valueOf(object.toString());
    }

    private Long getLongValueByKey(JSONObject jsonObject, String key) {
        Object object = jsonObject.get(key);
        if (object == null) return null;
        return Long.valueOf(object.toString());
    }

    private BigDecimal getBigDecimalValueByKey(JSONObject jsonObject, String key) {
        Object object = jsonObject.get(key);
        if (object == null) return null;
        return new BigDecimal(object.toString());
    }

    private String getStringValueByKey(JSONObject jsonObject, String key) {
        Object object = jsonObject.get(key);
        if (object == null) return null;
        return object.toString();
    }
}
