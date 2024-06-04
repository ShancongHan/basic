package com.example.basic.utils;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 系统操作流工具类
 *
 * @author han
 * @date 2023/5/24
 */
public class IOUtils {

    /**
     * 二进制输入流转byte[]数组
     * @param in 二进制输入流
     * @return byte[]数据
     * @throws IOException IOException
     */
    public static byte[] inputStreamToByteArray(InputStream in) throws IOException {
        return ByteStreams.toByteArray(in);
    }

    /**
     * 二进制输入流转String
     * @param in 二进制输入流
     * @return 字符串
     * @throws IOException IOException
     */
    public static String inputStreamToString(InputStream in) throws IOException {
        return CharStreams.toString(new InputStreamReader(in));
    }
}
