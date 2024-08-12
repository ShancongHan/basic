package com.example.basic.utils;

import com.alibaba.excel.util.StringUtils;
import com.example.basic.domain.ExpediaResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * @author han
 * @date 2024/7/31
 */
@Slf4j
@Component
public class HttpUtils {

    private final static String KEY = "2uv9s26jq8c2slabrnm10efnej";
    private final static String SECRET = "a8cnbrbiistkj";
    private final static String ENDPOINT = "https://test.ean.com/v3/";
    private final static String LANGUAGE = "en-US";
    private final static String CHINESE_LANGUAGE = "zh-CN";
    private final static String SHA_512 = "SHA-512";
    private final static String REGIONS = "regions";
    private final static String CHAINS = "chains";
    private final static String CONTENT = "properties/content";


    private final static String LANGUAGE_KEY = "language";
    private final static String SUPPLY_KEY = "supply_source";
    private final static String INCLUDE_KEY = "include";
    private final static String SUPPLY_VALUE = "expedia";
    private final static String INCLUDE_VALUE = "standard";
    private final static String INCLUDE_VALUE2 = "details";
    private final static String INCLUDE_VALUE3 = "property_ids";
    private final static String INCLUDE_VALUE4 = "property_ids_expanded";
    private final static String PAGE_HEAD_KEY = "Pagination-Total-Results";
    private final static String LINK_HEAD_KEY = "Link";
    private final static String LODA_HEAD_KEY = "Load";

    @Resource
    private OkHttpClient okHttpClient;

    public ExpediaResponse pullRegions(String sourceUrl) throws IOException {
        String url = StringUtils.isNotBlank(sourceUrl) ? sourceUrl : ENDPOINT + REGIONS;
        StopWatch watch = new StopWatch();
        watch.start("http发送请求");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter(INCLUDE_KEY, INCLUDE_VALUE2);
        urlBuilder.addQueryParameter(LANGUAGE_KEY, CHINESE_LANGUAGE);
        Request request = new Request.Builder()
                .url(urlBuilder.build().url())
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        watch.stop();
        watch.start("解析http内容");
        String link = response.header(LINK_HEAD_KEY);
        String nextPageUrl = StringUtils.isBlank(link) ? "" : link.substring(link.indexOf('<') + 1, link.indexOf('>'));
        String total = response.header(PAGE_HEAD_KEY);
        String load = response.header(LODA_HEAD_KEY);
        String body = response.body().string();
        watch.stop();
        ExpediaResponse real = ExpediaResponse.builder().build().setBody(body).setTotal(Integer.parseInt(total)).setNextPageUrl(nextPageUrl).setLoad(Integer.parseInt(load));
        log.info("http整体耗时:{}, {}", watch.getTotalTimeSeconds(), watch.prettyPrint());
        return real;
    }

    public ExpediaResponse pullContent(String sourceUrl) throws IOException {
        String url = StringUtils.isNotBlank(sourceUrl) ? sourceUrl : ENDPOINT + CONTENT;
        StopWatch watch = new StopWatch();
        watch.start("http发送请求");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter(SUPPLY_KEY, SUPPLY_VALUE);
        urlBuilder.addQueryParameter(LANGUAGE_KEY, CHINESE_LANGUAGE);
        Request request = new Request.Builder()
                .url(urlBuilder.build().url())
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        watch.stop();
        watch.start("解析http头");
        String link = response.header(LINK_HEAD_KEY);
        String nextPageUrl = StringUtils.isBlank(link) ? "" : link.substring(link.indexOf('<') + 1, link.indexOf('>'));
        String total = response.header(PAGE_HEAD_KEY);
        String load = response.header(LODA_HEAD_KEY);
        watch.stop();
        watch.start("解析http body");
        String body = response.body().string();
        watch.stop();
        ExpediaResponse real = ExpediaResponse.builder().build().setBody(body).setTotal(Integer.parseInt(total)).setNextPageUrl(nextPageUrl).setLoad(Integer.parseInt(load));
        log.info("http整体耗时:{}, {}", watch.getTotalTimeSeconds(), watch.prettyPrint());
        return real;
    }

    private String createSign() {
        long timestamp = new Date().getTime() / 1000;
        String toBeHashed = KEY + SECRET + timestamp;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = md.digest(toBeHashed.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return "EAN apikey=" + KEY + ",signature=" + sb + ",timestamp=" + timestamp;
    }

    public ExpediaResponse pullRegionsEn(String sourceUrl) throws Exception {
        String url = StringUtils.isNotBlank(sourceUrl) ? sourceUrl : ENDPOINT + REGIONS;
        StopWatch watch = new StopWatch();
        watch.start("http发送请求");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter(INCLUDE_KEY, INCLUDE_VALUE);
        urlBuilder.addQueryParameter(LANGUAGE_KEY, LANGUAGE);
        Request request = new Request.Builder()
                .url(urlBuilder.build().url())
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        watch.stop();
        watch.start("解析http内容");
        String link = response.header(LINK_HEAD_KEY);
        String nextPageUrl = StringUtils.isBlank(link) ? "" : link.substring(link.indexOf('<') + 1, link.indexOf('>'));
        String total = response.header(PAGE_HEAD_KEY);
        String load = response.header(LODA_HEAD_KEY);
        String body = response.body().string();
        watch.stop();
        ExpediaResponse real = ExpediaResponse.builder().build().setBody(body).setTotal(Integer.parseInt(total)).setNextPageUrl(nextPageUrl).setLoad(Integer.parseInt(load));
        log.info("http整体耗时:{}, {}", watch.getTotalTimeSeconds(), watch.prettyPrint());
        return real;
    }

    public String pullChain() throws Exception {
        String url =  ENDPOINT + CHAINS;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public String pullPropertyListByIds(List<String> propertyIds) throws Exception {
        // /properties/content?supply_source=expedia&property_id=107291302&language=zh-CN
        StringBuilder sb = new StringBuilder("?supply_source=expedia&language=zh-CN&");
        for (String propertyId : propertyIds) {
            sb.append("property_id=").append(propertyId).append("&");
        }
        String url =  ENDPOINT + CONTENT + sb.substring(0, sb.length() -1);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }
}
