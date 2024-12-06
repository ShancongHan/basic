package com.example.basic.helper;

import org.springframework.util.StringUtils;

/**
 * @author han
 * date 2024/5/31
 */
public class MappingGroupHelper {

    public static boolean compare(String name, String sysGroupName) {
        name = handler(name);
        sysGroupName = handler(sysGroupName);
        return name.equals(sysGroupName) || sysGroupName.contains(name);
    }

    public static String handler(String name){
        return StringUtils.hasLength(name) ? name.replaceAll("国际", "")
                .replaceAll("集团", "")
                .replaceAll("酒店", "")
                : "***";
    }

    public static boolean compare2(String name, String sysName) {
        name = handler(name);
        sysName = handler(sysName);
        if (sysName.contains("/")) {
            boolean bb = false;
            String[] split = sysName.split("/");
            for (String s : split) {
                bb = name.equals(s) || s.contains(name) || name.startsWith(s) || s.startsWith(name) || bb;
                if (bb) {
                    return true;
                }
            }
            return false;
        }
        return name.equals(sysName) || sysName.contains(name) || name.startsWith(sysName) || sysName.startsWith(name);
    }

    public static boolean compare3(String name, String sysName) {
        name = handler2(name);
        sysName = handler2(sysName);
        return name.equals(sysName) || name.startsWith(sysName) || sysName.startsWith(name);
    }

    public static String handler2(String name){
        return StringUtils.hasLength(name) ? name.replaceAll("区", "")
                .replaceAll("县", "")
                : "***";
    }
}
