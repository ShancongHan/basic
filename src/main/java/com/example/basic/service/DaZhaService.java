package com.example.basic.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.dao.BClassDao;
import com.example.basic.entity.BClass;
import com.example.basic.utils.IOUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/10/9
 */
@Service
public class DaZhaService {

    @Resource
    private BClassDao bClassDao;

    public void country() throws Exception {
        String fileName = "C:\\Users\\WST\\Documents\\WXWork\\1688858213530902\\Cache\\File\\2024-10\\新建 文本文档.txt";
        String json = IOUtils.inputStreamToString(new FileInputStream(fileName));
        JSONArray array = JSON.parseArray(json);
        List<String> countryCodeList = Lists.newArrayListWithCapacity(array.size());
        Map<String, String> countryMap = Maps.newHashMapWithExpectedSize(array.size());
        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            String countryCode = (String) jsonObject.get("id");
            String countryName = (String) jsonObject.get("value");
            countryMap.put(countryCode, countryName);
            countryCodeList.add(countryCode);
        }
        List<BClass> bClasses = bClassDao.selectCountryList();
        System.out.println("文件中共有国家 "+countryMap.size());
        System.out.println("数据库中共有国家 "+bClasses.size());

        Map<String, String> collect = bClasses.stream().collect(Collectors.toMap(BClass::getBy3, BClass::getCName));
        List<String> notFoundList = Lists.newArrayList();
        for (BClass bClass : bClasses) {
            String cName = bClass.getCName();
            String code = bClass.getBy3();
            if (!countryMap.containsKey(code)) {
                notFoundList.add(code);
            }
        }
        System.out.println("系统多的"+ notFoundList.size() + JSON.toJSONString(notFoundList));

        List<String> notFoundList1 = Lists.newArrayList();
        for (String s : countryCodeList) {
            if (!collect.containsKey(s)) {
                notFoundList1.add(s);
            }
        }
        System.out.println("文件多的"+ notFoundList1.size() + JSON.toJSONString(notFoundList1));
    }
}
