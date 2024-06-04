package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.example.basic.annotation.FastXmlBean;
import com.example.basic.dao.BCityNewlyDao;
import com.example.basic.domain.City;
import com.example.basic.utils.IOUtils;
import com.github.fastxml.FastXmlFactory;
import com.github.fastxml.FastXmlParser;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author han
 * @ Date 2024/4/12
 */
@Service
public class DataService {

    @Resource
    private BCityNewlyDao bCityNewlyDao;

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    public void analyze() throws Exception {
        String fileName = "C:\\wst_han\\打杂\\国内城市及地标.xml";
        byte[] bytes = IOUtils.inputStreamToByteArray(new FileInputStream(fileName));
        FastXmlParser parser = FastXmlFactory.newInstance(bytes, StandardCharsets.UTF_8);
        List<City> cityList = parser(parser, City.class);
        System.out.println(JSON.toJSONString(cityList));
        redisTemplate.opsForValue().set("bdc.rest.test.testData111", JSON.toJSONString(cityList));


    }

    private List<City> parser(FastXmlParser parser, Class<City> clazz) throws Exception {
        List<City> cityList = Lists.newArrayList();

        Multimap<String, String> elementMap = ArrayListMultimap.create();
        Map<String, Field> fieldMap = Maps.newHashMap();
        Map<String, Field> subFieldMap = Maps.newHashMap();

        for (Field field : clazz.getDeclaredFields()) {
            FastXmlBean fastXmlBean = field.getAnnotation(FastXmlBean.class);
            String element = fastXmlBean.element();
            String attribute = fastXmlBean.attribute();
            boolean subElement = fastXmlBean.subElement();
            if (subElement) {
                String start = fastXmlBean.start();
                subFieldMap.put(start, field);
            } else {
                elementMap.put(element, attribute);
                fieldMap.put(element + attribute, field);
            }
        }

        City city = clazz.getDeclaredConstructor().newInstance();
        do {
            do {
                parser.next();
                String currentElement = parser.getStringWithDecoding();
                if ("CityPorList".equals(currentElement) && parser.getCurrentEvent() == FastXmlParser.END_TAG) break;
                if (!elementMap.containsKey(currentElement) && !subFieldMap.containsKey(currentElement)) continue;
                if (parser.getCurrentEvent() == FastXmlParser.START_TAG) {
                    if (elementMap.containsKey(currentElement)) {
                        Collection<String> attributes = elementMap.get(currentElement);
                        do {
                            parser.next();
                            String currentAttributes = parser.getString();
                            if (attributes.contains(currentAttributes) && parser.next() == FastXmlParser.ATTRIBUTE_VALUE) {
                                Field field = fieldMap.get(currentElement + currentAttributes);
                                field.setAccessible(Boolean.TRUE);
                                field.set(city, parser.getStringWithDecoding());
                            }
                        } while (parser.getNextEvent() != FastXmlParser.START_TAG);
                    }
                    if (subFieldMap.containsKey(currentElement)) {
                        Field field = subFieldMap.get(currentElement);
                        field.setAccessible(Boolean.TRUE);
                        field.set(city, parserSubElement(parser, field));
                    }
                }
            } while (!(parser.isMatch("CityPor".getBytes()) && parser.getCurrentEvent() == FastXmlParser.END_TAG));
            cityList.add(city);
            city = clazz.getDeclaredConstructor().newInstance();
        } while (parser.getNextEvent() != FastXmlParser.END_DOCUMENT);
        return cityList;
    }

    private Object parserSubElement(FastXmlParser parser, Field field) throws Exception {
        FastXmlBean fastXmlBean = field.getAnnotation(FastXmlBean.class);
        String start = fastXmlBean.start();
        String stop = fastXmlBean.stop();
        Class<?> type = field.getType();
        Object fieldObject = type.getDeclaredConstructor().newInstance();
        Field porListField = type.getDeclaredField("porList");
        List<Object> objects = parserList(parser, porListField, start, stop);
        porListField.setAccessible(Boolean.TRUE);
        porListField.set(fieldObject, objects);
        return fieldObject;
    }

    private List parserList(FastXmlParser parser, Field porListField, String start, String stop) throws Exception {
        String childStart = porListField.getAnnotation(FastXmlBean.class).start();
        boolean multi = porListField.getAnnotation(FastXmlBean.class).multi();
        boolean selfClose = porListField.getAnnotation(FastXmlBean.class).selfClose();
        Class<?> actualTypeArgument = (Class<?>) ((ParameterizedType) porListField.getGenericType()).getActualTypeArguments()[0];


        List list = Lists.newArrayList();
        Map<String, Field> fieldMap = Maps.newHashMap();
        for (Field field : actualTypeArgument.getDeclaredFields()) {
            FastXmlBean fastXmlBean1 = field.getAnnotation(FastXmlBean.class);
            String xml1 = fastXmlBean1.attribute();
            fieldMap.put(xml1, field);
        }
        do {
            Object fieldObject = actualTypeArgument.getDeclaredConstructor().newInstance();
            do {
                String now = parser.getString();
                if (fieldMap.containsKey(now) && parser.next() == FastXmlParser.ATTRIBUTE_VALUE) {
                    Field field = fieldMap.get(now);
                    field.setAccessible(Boolean.TRUE);
                    field.set(fieldObject, parser.getStringWithDecoding());
                }
                parser.next();
            } while (parser.getCurrentEvent() != FastXmlParser.END_TAG_WITHOUT_TEXT);
            list.add(fieldObject);
            parser.next();
        } while (!parser.isMatch(stop.getBytes()));
        return list;
    }
}
