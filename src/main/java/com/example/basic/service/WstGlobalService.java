package com.example.basic.service;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.*;
import com.example.basic.entity.*;
import com.example.basic.utils.IOUtils;
import com.example.basic.utils.TimeUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author han
 * @date 2024/12/19
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
        initImages();
    }

    private void initImages() throws Exception{
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\EPS梳理\\静态数据\\images.json";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        List<WstHotelGlobalImagesDictionary> list = parseImages(json);
        wstHotelGlobalImagesDictionaryDao.saveBatch(list);
    }

    private List<WstHotelGlobalImagesDictionary> parseImages(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject categories = (JSONObject) jsonObject.get("statistics");
        Collection<Object> values = categories.values();
        List<WstHotelGlobalImagesDictionary> list = Lists.newArrayListWithCapacity(values.size());
        Date date = new Date();
        Date expediaFileTime = TimeUtils.parseStringToDate("2024-10-21", "yyyy-MM-dd");
        for (Object value : values) {
            JSONObject valueObject = (JSONObject) value;
            String id = valueObject.get("id").toString();
            String nameEn = valueObject.get("name").toString();
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
}
