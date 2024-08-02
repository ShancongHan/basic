package com.example.basic.service;

import com.example.basic.dao.SpiderAirportInfoDao;
import com.example.basic.entity.SpiderAirportInfo;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/7/1
 */
@Slf4j
@Service
public class AirportCrawlerService {

    @Resource
    private SpiderAirportInfoDao spiderAirportInfoDao;

    public void airport() {
        List<SpiderAirportInfo> spiderAirportInfoList = Lists.newArrayListWithCapacity(10000);
        String baseUrl = "https://www.flightconnections.com";
        String all = "/airports-by-country";
        Connection connection = Jsoup.connect(baseUrl + all)
                .timeout(10000)
                .method(Connection.Method.POST);
        connection.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        Document doc;
        try {
            doc = connection.execute().parse();
        } catch (IOException e) {
            log.error("can't access url: {}, detail error: {} ", baseUrl + all, Throwables.getStackTraceAsString(e));
            return;
        }
        Elements tables = doc.getElementsByClass("airport-list");
        for (Element table : tables) {
            Elements liElements = table.select("li");
            for (Element liElement : liElements) {

                Elements a = liElement.select("a");
                String href = a.get(0).attr("href");
                Elements spans = liElement.select("span");
                String countryName = spans.get(1).text();
                //log.info("get country: {} with url: {}", countryName, href);
                spiderOneCountry(baseUrl + href, href.split("-")[href.split("-").length - 1], countryName, spiderAirportInfoList);
            }
        }
        System.out.println(spiderAirportInfoList.size());
        saveBatch(spiderAirportInfoList);
    }

    private void saveBatch(List<SpiderAirportInfo> insertList) {
        int start = 0;
        for (int j = 0; j < insertList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<SpiderAirportInfo> list = insertList.subList(start, j);
                spiderAirportInfoDao.saveBatch(list);
                start = j;
            }
        }
        List<SpiderAirportInfo> provinceList = insertList.subList(start, insertList.size());
        if (CollectionUtils.isNotEmpty(provinceList)) {
            spiderAirportInfoDao.saveBatch(provinceList);
        }

    }

    private void spiderOneCountry(String url, String countryCode, String countryName, List<SpiderAirportInfo> spiderAirportInfoList) {
        Connection connection = Jsoup.connect(url)
                .timeout(10000)
                .method(Connection.Method.POST);
        connection.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        Document doc;
        try {
            doc = connection.execute().parse();
        } catch (IOException e) {
            log.error("can't access url: {}, detail error: {} ", url, Throwables.getStackTraceAsString(e));
            return;
        }
        Elements ulList = doc.select("ul");
        for (Element ul : ulList) {
            Elements liElements = ul.select("li");
            for (Element liElement : liElements) {
                SpiderAirportInfo info = new SpiderAirportInfo();
                Element city = liElement.select("p").get(0);
                String cityName = city.text();
                String airportCode = city.select("span").get(0).text();
                String airportName = liElement.select("p").get(1).select("span").text();
                //log.info("get cityName: {} have airport: {},{}", cityName, airportName, airportCode);
                info.setCountryCode(countryCode.toUpperCase());
                info.setCountryNameEn(countryName);
                info.setCityNameEn(cityName.replaceAll(airportCode, "").replaceAll(" ", ""));
                info.setAirportCode(airportCode);
                info.setAirportNameEn(airportName);
                spiderAirportInfoList.add(info);
            }
        }
    }

    public void airportCn() {
        List<SpiderAirportInfo> spiderAirportInfos = spiderAirportInfoDao.selectAll();
        Map<String, List<SpiderAirportInfo>> countryCodeMap = spiderAirportInfos.stream().collect(Collectors.groupingBy(SpiderAirportInfo::getCountryCode));
        String baseUrl = "https://www.flightconnections.com";
        String all = "/cn/机场按国家";
        Connection connection = Jsoup.connect(baseUrl + all)
                .timeout(10000)
                .method(Connection.Method.POST);
        connection.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        Document doc;
        try {
            doc = connection.execute().parse();
        } catch (IOException e) {
            log.error("can't access url: {}, detail error: {} ", baseUrl + all, Throwables.getStackTraceAsString(e));
            return;
        }
        Elements tables = doc.getElementsByClass("airport-list");
        for (Element table : tables) {
            Elements liElements = table.select("li");
            for (Element liElement : liElements) {
                Elements a = liElement.select("a");
                String href = a.get(0).attr("href");
                Elements spans = liElement.select("span");
                String countryName = spans.get(1).text();
                log.info("get country: {} with url: {}", countryName, href);
                String countryCode = href.split("-")[href.split("-").length - 1].toUpperCase();
                spiderOneCountryAndUpdate(baseUrl + href, countryName, countryCodeMap.get(countryCode));
            }
        }
    }

    private void spiderOneCountryAndUpdate(String url, String countryName, List<SpiderAirportInfo> spiderAirportInfos) {
        Map<String, SpiderAirportInfo> airportCodeMap = spiderAirportInfos.stream().collect(Collectors.toMap(SpiderAirportInfo::getAirportCode, Function.identity()));
        String realUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
        Connection connection = Jsoup.connect(realUrl)
                .timeout(10000)
                .method(Connection.Method.POST);
        connection.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        Document doc;
        try {
            doc = connection.execute().parse();
        } catch (IOException e) {
            log.error("can't access url: {}, detail error: {} ", url, Throwables.getStackTraceAsString(e));
            return;
        }
        Elements ulList = doc.select("ul");
        for (Element ul : ulList) {
            Elements liElements = ul.select("li");
            for (Element liElement : liElements) {
                Element city = liElement.select("p").get(0);
                String cityName = city.text();
                String airportCode = city.select("span").get(0).text();
                SpiderAirportInfo info = airportCodeMap.get(airportCode);
                info.setCountryName(countryName);
                info.setCityName(cityName.replaceAll(airportCode, "").replaceAll(" ", ""));
                spiderAirportInfoDao.update(info);
            }
        }
    }
}
