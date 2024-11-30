package com.example.basic.domain;

import com.alibaba.fastjson2.JSON;
import com.example.basic.utils.HttpUtils;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author han
 * @date 2024/11/20
 */
@Slf4j
@Component
public class EsSupport {

    @Resource
    private HttpUtils httpUtils;

    private static final String IP = "http://47.112.121.211:";
    private static final Integer NORMAL_STATUS = 1000;
    private static final String TEST_PORT = "8080";
    private static final String URL = "/es-hotel/find-by-filter-for-mapping";

    public String findCity(String jsonBody) {
        String url = IP + TEST_PORT;
        return queryData(jsonBody, url + URL, false);
    }

    private Map<String, String> commonHeader() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("sign", "dbb73a39868341839b00535d3bed9d74");
        return headers;
    }

    public String queryData(String jsonBody, String url, boolean printLog) {
        try {
            String resJson = httpUtils.queryEs(url, commonHeader(), jsonBody);
            EsResult result = JSON.parseObject(resJson, EsResult.class);
            Integer status = result.getStatus();
            if (!NORMAL_STATUS.equals(status)) {
                throw new Exception(result.getMessage());
            }
            return result.getData();
        } catch (Exception e) {
            log.error("请求es报错" + url + "原始返回：" + e.getMessage());
            System.out.println("请求es报错" + url + "原始返回：" + e.getMessage());
        }
        return null;
    }
}
