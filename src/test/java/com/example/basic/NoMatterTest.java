package com.example.basic;

import com.alibaba.excel.util.DateUtils;
import com.example.basic.helper.MappingScoreHelper;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author han
 * @date 2024/5/23
 */
public class NoMatterTest {

    public static void main(String[] args) throws Exception {
        String dateTime = "2023-11-23T09:05:37.357Z";
        dateTime = dateTime.replace("T", " ");
        dateTime = dateTime.substring(0, dateTime.indexOf("."));
        System.out.println(dateTime);
        System.out.println(DateUtils.parseDate(dateTime));


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
