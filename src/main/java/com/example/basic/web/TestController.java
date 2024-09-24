package com.example.basic.web;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.hankcs.hanlp.restful.HanLPClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author han
 * @date 2024/9/14
 */
@RestController
@RequestMapping(value = "test")
public class TestController {

    @PostMapping(value = "fenci")
    public Map<String, List> fenci(String key) {
        HanLPClient client = new HanLPClient("https://hanlp.hankcs.com/api", null); // Replace null with your auth
        String[] tasks = new String[2];
        tasks[0] = "tok/fine";
        tasks[1] = "tok/coarse";
        try {
            return client.parse(key, tasks, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "fenci2")
    public List<String> fenci2(String key) {
        String url = "http://www.foryet.net/api/actions.aspx?action=segwords";
        Connection connection = Jsoup.connect(url).data("content", key).timeout(10000)
                .method(Connection.Method.POST);
        Document doc = null;
        try {
            doc = connection.execute().parse();
            String data = doc.text();
            String body = data.substring(data.indexOf("\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n全季"), data.indexOf("\\n\\n\\n分词次数统计：\\n\\n\\n\\n"));
            body = body.replace("\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n", "");
            String[] split = body.split(" / ");
            return Arrays.asList(split);
        } catch (IOException e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
        return null;
    }
}
