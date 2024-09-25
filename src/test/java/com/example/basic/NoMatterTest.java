package com.example.basic;

import com.alibaba.excel.util.DateUtils;
import com.example.basic.helper.MappingScoreHelper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author han
 * @date 2024/5/23
 */
public class NoMatterTest {

    public static void main(String[] args) throws Exception {
        int i = 585;
        int j = 93254;
        double x = (float) i / (double) j * 100;
        NumberFormat format = NumberFormat.getInstance();
        String format1 = format.format(x);
        System.out.println(x);
        System.out.println(format1);

        System.out.println(2<<6);

        List<String> compNos = Lists.newArrayList("1");
        addCompNos(compNos,"111");
        System.out.println(compNos);
//        String x = "[[\"汉庭\",\"张家港\",\"高铁站\",\"塘桥\",\"酒店\"]]";

        /*String x = """
                {"data":"\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n全季 / 酒店 / ( / 霍尔果斯 / 国 / 门店 / ) \\n\\n\\n分词次数统计：\\n\\n\\n\\n \\n全季 = 1 次；\\n酒店 = 1 次；\\n( = 1 次；\\n霍尔果斯 = 1 次；\\n国 = 1 次；\\n门店 = 1 次；\\n) = 1 次；\\n \\n\\n\\n\\n","code":100,"msg":"获取数据成功！"}
                """;
        String xx = x.substring(x.indexOf("\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n全季"), x.indexOf("\\n\\n\\n分词次数统计：\\n\\n\\n\\n"));
        xx = xx.replace("\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n", "");
        String s = xx.replaceAll("<em class=\"\\\\&quot;text-color-999\\\\&quot;\"> / </em>", ",");
        System.out.println(s);*/
        /*String dateTime = "2023-11-23T09:05:37.357Z";
        dateTime = dateTime.replace("T", " ");
        dateTime = dateTime.substring(0, dateTime.indexOf("."));
        System.out.println(dateTime);
        System.out.println(DateUtils.parseDate(dateTime));*/


        /*String s = handlerTel("63-22498745");
        String s2 = handlerTel("+63 2 22498745");
        System.out.println(s);
        System.out.println(s2);*/
        /*System.out.println(10216%10);

        String s= ",,,,";
        System.out.println(s.replaceAll(",",""));
        System.out.println(s);*/
        //System.out.println(URLDecoder.decode("/cn/%E6%9C%BA%E5%9C%BA%E5%9C%A8-%E4%B8%8D%E4%B8%B9-bt", StandardCharsets.UTF_8));
        //System.out.println(510100 % 10);

       /* String str = "";
        row80Char(str, '1');*/
        /*MappingScoreHelper.calculateScore("Hotel Beni Hamad","36.068446"
                ,"4.765295","01, Rue Frantz Fanon Bordj Bou Arreridj Algérie","00213-770521828"
                ,"Bodrum Park Resort",new BigDecimal("36.982981")
                ,new BigDecimal("7.561266"),"塞赖迪Yalıciftlik Mevkii 48410 Bodrum Turkey, 阿尔及利亚","0");*/
    }

    private static void addCompNos(List<String> compNos, String s) {
        if (CollectionUtils.isEmpty(compNos)){
            compNos = new ArrayList<>();
        }
        if (StringUtils.hasLength(s)){
            compNos.add("看吧");
        }
    }

    public static String handlerTel(String telephone){
        return StringUtils.hasLength(telephone) ? telephone.replaceAll("-", "")
                .replaceAll("\\+", "")
                .replaceAll(" ", " ")
                .replaceAll(" +", "")
                .replaceAll("\\s+", "")
                .toUpperCase() : "***";
    }

    public static List<String> row80Char(String str, Character featureSymbol) {
        int count = 0;
        int last1Index = -1;
        int subStartIndex = 0;
        List<String> tmpStrList = new ArrayList<>();
        for (int k = 0; k < str.length(); k++) {
            char ch = str.charAt(k);
            if (LdkjStr.isChinese(ch)) {
                count += 2;
            } else {
                count++;
            }
            if (featureSymbol != null) {
                if (featureSymbol == ch) {
                    last1Index = k;
                }
            } else {
                last1Index = k;
            }

            if (count >= 80) {
                tmpStrList.add(str.substring(subStartIndex, last1Index));
                count = 0;
                k = subStartIndex = last1Index;
            }
        }
        if (tmpStrList.size() > 0) {
            if (subStartIndex < str.length() - 1) {
                tmpStrList.add(str.substring(subStartIndex));
            }
        }
        return tmpStrList;
    }



}
