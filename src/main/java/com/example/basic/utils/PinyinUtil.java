package com.example.basic.utils;


import com.alibaba.excel.util.StringUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * 获取汉字首字母拼音类
 *
 * @author zhangfan
 */
public class PinyinUtil {

    // 返回中文的首字母
    public static String getPinYinHeadChar(String str) {
        if (StringUtils.isNotBlank(str)) {
            StringBuilder convert = new StringBuilder();
            for (int j = 0; j < str.length(); j++) {
                char word = str.charAt(j);
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
                if (pinyinArray != null) {
                    convert.append(pinyinArray[0].charAt(0));
                } else {
                    convert.append(word);
                }
            }
            return convert.toString();
        } else {
            return "";
        }
    }

    /**
     * 汉字转换为汉语拼音，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        if(StringUtils.isBlank(chines)){
            return "";
        }
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                if (isChinese(nameChar[i])) {
                    try {
                        pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
                    } catch (Exception e) {
                        pinyinName += nameChar[i];
                    }
                } else {
                    pinyinName += nameChar[i];
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    public static boolean isChinese(char c) {
        return (c + "").matches("[\u4E00-\u9FA5]");
    }

    public static void main(String[] args) {
        System.out.println(converterToSpell("全家桶"));
    }
}
