package com.example.basic.utils;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.domain.DongChengGnResponse;
import com.example.basic.domain.DongChengHotelRequest;
import com.example.basic.domain.ExpediaResponse;
import com.example.basic.domain.Region;
import com.example.basic.entity.ExpediaCountry;
import com.example.basic.entity.ExpediaRegions;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private final static String PRICE = "properties/availability";


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

    private static final String DONGCHENG_APPID = "QHhkzl";
    private static final String DONGCHENG_KEY = "QHHKduqiueysszlzlzl";
    private static final String DONGCHENG_URL = "https://ota.dossen.com";

    @Resource
    private OkHttpClient okHttpClient;
    
    public ExpediaResponse pullRegions(String sourceUrl) throws IOException {
        String url = StringUtils.isNotBlank(sourceUrl) ? sourceUrl : ENDPOINT + REGIONS + "?include=standard&language=zh-CN&type=country";
        StopWatch watch = new StopWatch();
        watch.start("http发送请求");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter(INCLUDE_KEY, INCLUDE_VALUE2);
        urlBuilder.addQueryParameter(LANGUAGE_KEY, CHINESE_LANGUAGE);
        Request request = new Request.Builder()
                .url(url)
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
        String url = ENDPOINT + CHAINS;
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
        String url = ENDPOINT + CONTENT + sb.substring(0, sb.length() - 1);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public String pullHotelDetailListByIdsEn(List<String> hotelIds) throws Exception {
        return pullHotelDetailListByIds(hotelIds, LANGUAGE);
    }

    public String pullHotelDetailListByIdsZh(List<String> hotelIds) throws Exception {
        return pullHotelDetailListByIds(hotelIds, CHINESE_LANGUAGE);
    }
    public String pullHotelDetailListByIds(List<String> hotelIds, String language) throws Exception {
        StringBuilder sb = new StringBuilder("?supply_source=expedia&")
                .append(LANGUAGE_KEY).append("=").append(language).append("&");
        for (String hotelId : hotelIds) {
            sb.append("property_id=").append(hotelId).append("&");
        }
        String url = ENDPOINT + CONTENT + sb.substring(0, sb.length() - 1);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public DongChengGnResponse getDongChengCity() {
        String url = DONGCHENG_URL + "/Api/GetCitiesList?TokenID=UUhoa3psfFFISEtkdXFpdWV5c3N6bHpsemw";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return JSONObject.parseObject(response.body().string(), DongChengGnResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DongChengGnResponse getDongChengHotel(DongChengHotelRequest requestDto) {
        String url = DONGCHENG_URL + "/Api/GetHotelInfo?TokenID=UUhoa3psfFFISEtkdXFpdWV5c3N6bHpsemw";
        RequestBody body = RequestBody.create(JSON.toJSONBytes(requestDto));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return JSONObject.parseObject(response.body().string(), DongChengGnResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String pullMinPrice(String xml) {
        String url = "https://us.dotwconnect.com/gatewayV4.dotw";
       // RequestBody body = RequestBody.create(xml.getBytes(StandardCharsets.UTF_8));
        RequestBody body = RequestBody.create(xml, MediaType.parse("application/xml"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/xml")
                .header("Connection", "keep-alive")
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String pullContentEn(String hotelId) {
        return pullContent(hotelId, "en-US");
    }

    public String pullContentZh(String hotelId) {
        return pullContent(hotelId, "zh-CN");
    }
    public String pullContent(String hotelId, String language) {
        String url = ENDPOINT + CONTENT + "?language=" + language + "&supply_source=expedia&property_id=" + hotelId;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response;
        try {
            response = call.execute();
            //log.info("{}酒店请求了{}", hotelId, language);
            ResponseBody body = response.body();
            if (body == null) return null;
            return body.string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String pullPrice(String hotelId, String checkin, String checkout) {
        return pullPrice(hotelId, "hotel_only", checkin, checkout);
    }

    public String pullPrice2(String hotelId, String checkin, String checkout, String countryCode) {
        String url = ENDPOINT + PRICE + "?checkin=" + checkin +
                "&checkout=" + checkout + "&property_id=" + hotelId + "&sales_environment=hotel_only" +
                "&occupancy=1&sales_channel=website&language=en-US&rate_option=member&payment_terms=0" +
                "&rate_plan_count=10&travel_purpose=business&country_code="+countryCode+"&partner_point_of_sale=VCC_INTERNAL_SA_PKG_MOD" +
                "&currency=USD&billing_terms=VCC&supply_source=expedia";
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response;
        try {
            response = call.execute();
            ResponseBody body = response.body();
            if (body == null) return null;
            return body.string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String pullPricePackage(String hotelId, String checkin, String checkout) {
        return pullPrice(hotelId, "hotel_package", checkin, checkout);
    }

    public String pullPrice(String hotelId, String salesEnvironment, String checkin, String checkout) {
        String url = ENDPOINT + PRICE + "?checkin=" + checkin +
                "&checkout=" + checkout + "&property_id=" + hotelId + "&sales_environment=" + salesEnvironment +
                "&occupancy=1&sales_channel=website&language=en-US&rate_option=member&payment_terms=0" +
                "&rate_plan_count=10&travel_purpose=business&country_code=CN&partner_point_of_sale=VCC_INTERNAL_SA_PKG_MOD" +
                "&currency=USD&billing_terms=VCC&supply_source=expedia";
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response;
        try {
            response = call.execute();
            ResponseBody body = response.body();
            if (body == null) return null;
            return body.string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ExpediaResponse pullRegionsV2(String sourceUrl, String countryCode) throws Exception {
        String url = StringUtils.isNotBlank(sourceUrl) ? sourceUrl : ENDPOINT + REGIONS + "?include=standard&language=en-US&country_code=" + countryCode;
        /*StopWatch watch = new StopWatch();
        watch.start("http发送请求");*/
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        String link = response.header(LINK_HEAD_KEY);
        String nextPageUrl = StringUtils.isBlank(link) ? "" : link.substring(link.indexOf('<') + 1, link.indexOf('>'));
        String total = response.header(PAGE_HEAD_KEY);
        String load = response.header(LODA_HEAD_KEY);
        String body = response.body().string();
        //watch.stop();
        ExpediaResponse real = ExpediaResponse.builder().build().setBody(body).setTotal(total == null ? 0 : Integer.parseInt(total)).setNextPageUrl(nextPageUrl).setLoad(load == null ? 0 : Integer.parseInt(load));
        //log.info("http耗时:{}", watch.getTotalTimeSeconds());
        return real;
    }

    public ExpediaCountry pullCountryCode(String expediaId) {
        String url = "https://test.ean.com/v3/regions/" + expediaId + "?language=en-US&include=details";
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String body = response.body().string();
            Region region = JSON.parseObject(body, Region.class);
            String countryCode = region.getCountry_code();
            ExpediaCountry country = new ExpediaCountry();
            country.setExpediaId(expediaId);
            country.setCode(countryCode);
            return country;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ExpediaRegions pullRegion(String regionId, Long id) {
        String url = "https://test.ean.com/v3/regions/" + regionId + "?language=zh-CN&include=details";
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String body = response.body().string();
            ExpediaRegions expediaRegions = new ExpediaRegions();
            expediaRegions.setId(id);
            expediaRegions.setRegionId(regionId);
            if (StringUtils.isBlank(body) || "{}".equals(body)) {
                expediaRegions.setHasZh(false);
                return expediaRegions;
            }
            Region region = JSON.parseObject(body, Region.class);
            String name = region.getName();
            expediaRegions.setName(name);
            if (StringUtils.isNotBlank(name)) {
                expediaRegions.setHasZh(true);
            }
            expediaRegions.setFullName(region.getName_full());
            expediaRegions.setCountrySubdivisionCode(region.getCountry_subdivision_code());
            expediaRegions.setCenterLatitude(BigDecimal.valueOf(region.getCoordinates().getCenter_latitude()));
            expediaRegions.setCenterLongitude(BigDecimal.valueOf(region.getCoordinates().getCenter_longitude()));
            if (CollectionUtils.isNotEmpty(region.getCategories())) {
                expediaRegions.setCategories(region.getCategories().toString());
            }
            if (CollectionUtils.isNotEmpty(region.getTags())) {
                expediaRegions.setTags(region.getTags().toString());
            }
            expediaRegions.setAncestors(JSON.toJSONString(region.getAncestors()));
            expediaRegions.setDescendants(JSON.toJSONString(region.getDescendants()));
            return expediaRegions;
        } catch (IOException e) {
            log.info("转换异常{}", Throwables.getStackTraceAsString(e));
            return null;
        }
    }

    public ExpediaResponse pullProperty(String sourceUrl, String countryCode) {
        String url = StringUtils.isNotBlank(sourceUrl) ? sourceUrl : "https://test.ean.com/v3/regions?include=property_ids&language=en-US&country_code=" + countryCode;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String link = response.header(LINK_HEAD_KEY);
            String nextPageUrl = StringUtils.isBlank(link) ? "" : link.substring(link.indexOf('<') + 1, link.indexOf('>'));
            String total = response.header(PAGE_HEAD_KEY);
            String load = response.header(LODA_HEAD_KEY);
            String body = response.body().string();
            //watch.stop();
            ExpediaResponse real = ExpediaResponse.builder().build().setBody(body).setTotal(total == null ? 0 : Integer.parseInt(total)).setNextPageUrl(nextPageUrl).setLoad(load == null ? 0 : Integer.parseInt(load));
            //log.info("http耗时:{}", watch.getTotalTimeSeconds());
            return real;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String queryEs(String url, Map<String, String> map, String jsonBody) throws Exception {
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ExpediaResponse pullRegionStandardEn(String sourceUrl, String countryCode) {
        return pullRegionStandard(sourceUrl, countryCode, LANGUAGE);
    }

    public ExpediaResponse pullRegionStandardZh(String sourceUrl, String countryCode) {
        return pullRegionStandard(sourceUrl, countryCode, CHINESE_LANGUAGE);
    }

    public ExpediaResponse pullRegionStandard(String sourceUrl, String countryCode, String language) {
        String url = StringUtils.isNotBlank(sourceUrl) ? sourceUrl : ENDPOINT + REGIONS + "?include=standard&language=" + language + "&country_code=" + countryCode;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String link = response.header(LINK_HEAD_KEY);
            String nextPageUrl = StringUtils.isBlank(link) ? "" : link.substring(link.indexOf('<') + 1, link.indexOf('>'));
            String total = response.header(PAGE_HEAD_KEY);
            String load = response.header(LODA_HEAD_KEY);
            String body = response.body().string();
            return ExpediaResponse.builder().build().setBody(body).setTotal(total == null ? 0 : Integer.parseInt(total)).setNextPageUrl(nextPageUrl).setLoad(load == null ? 0 : Integer.parseInt(load));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String pullRegionProperty(String regionId) {
        String url = ENDPOINT + REGIONS + "/" + regionId + "?include=property_ids&language=en-US";
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", createSign())
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String pullDidaCountryEn() {
        return pullDidaCountry("en-US");
    }

    public String pullDidaCountryCn() {
        return pullDidaCountry("zh-CN");
    }

    public String pullDidaCountry(String language) {
        String url = "https://static-api.didatravel.com/api/v1/region/countries?language=" + language;
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", "Basic V1NUTFk6V1NUTFlfa2V5")
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String pullDidaHotelId() {
        String url = "https://static-api.didatravel.com/api/v1/hotel/list?countryCode=CN&lastUpdateTime=1735097363&language=en-US";
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", "Basic V1NUTFk6V1NUTFlfa2V5")
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String pullDidaHotelInfoEn(List<Integer> hotelIds) {
        return pullDidaHotelInfo(hotelIds,"en-US");
    }

    public String pullDidaHotelInfoCn(List<Integer> hotelIds) {
        return pullDidaHotelInfo(hotelIds,"zh-CN");
    }

    public String pullDidaHotelInfo(List<Integer> hotelIds, String language) {
        String url = "https://static-api.didatravel.com/api/v1/hotel/details";
        String jsonBodyFormat = "{\"language\":\"%s\",\"hotelIds\":%s}";
        String jsonBody = String.format(jsonBodyFormat, language, hotelIds.toString());
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .header("Authorization", "Basic V1NUTFk6V1NUTFlfa2V5")
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
