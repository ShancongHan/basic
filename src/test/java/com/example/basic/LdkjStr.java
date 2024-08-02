package com.example.basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LdkjStr {

    /**
     * 是否全部是汉字
     */
    public static boolean isChinese(char ch) {
        Pattern p1 = Pattern.compile("^[\u4e00-\u9fa5]+$");
        Matcher m1 = p1.matcher(ch + "");
        return m1.find();
    }
}
