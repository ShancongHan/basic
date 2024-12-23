package com.example.basic.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * 时间工具类
 */
public class TimeUtils {

    private static final DateTimeFormatter yyyyMMddHHmmssFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter YYYY_MM_DD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter yyMMddFormatter = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter yyyyMMddFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter MM_DD_YYYY_FORMATTER=DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter yyyyMMddHHmmFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /**
     * 获取当前时间戳(s)
     *
     * @return 时间戳
     */
    public static String timestamp() {
        Long time = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return String.valueOf(time);
    }

    /**
     * 获取当前时间戳(s)
     *
     * @return 时间戳
     */
    public static Long times() {
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static Date now() {
        return parseDate(LocalDateTime.now());
    }

    /**
     * 获取今日日期
     * @return 今日日期
     */
    public static Date today() {
        return parseDate(LocalDate.now());
    }

    public static Long yesterday() {
        return LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static String getyyMMddDateString() {
        return yyMMddFormatter.format(LocalDateTime.now().atOffset(ZoneOffset.of("+8")));
    }

    public static String getyyyyMMddDateString() {
        return yyyyMMddFormatter.format(LocalDateTime.now().atOffset(ZoneOffset.of("+8")));
    }

    public static String getTomorrowYyMMddDateString() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusDays(1L);
        return yyMMddFormatter.format(localDateTime.atOffset(ZoneOffset.of("+8")));
    }

    public static Date parseStringToDate(String date, String formatter) {
        if (StringUtils.isBlank(date)) return null;
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(formatter));
        return Date.from(localDate.atStartOfDay(ZoneOffset.of("+8")).toInstant());
    }

    public static Long parseStringToLong(String date, String formatter) {
        if (StringUtils.isBlank(date)) return null;
        LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(formatter));
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获取time后的时间
     *
     * @param time 过期时间
     * @return 过期日期
     */
    public static Date getExpireDateTime(Long time) {
        return parseDate(LocalDateTime.now().plusSeconds(time));
    }

    private static Date parseDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneOffset.ofHours(8)).toInstant();
        return Date.from(instant);
    }

    private static Date parseDate(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay().atZone(ZoneOffset.ofHours(8)).toInstant();
        return Date.from(instant);
    }

    /**
     * 时间戳转化为当天开始的时间戳
     *
     * @param time 时间戳
     * @return yyyyMMddHHmmss String
     */
    public static String getStartOfDay(Long time) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        return yyyyMMddHHmmssFormatter.format(localDateTime.with(LocalTime.MIN));
    }

    public static Date getStartOfDay(LocalDate localDate) {
        return parseDate(localDate.atStartOfDay());
    }

    /**
     * 获取今日开始时间
     * @return 开始时间
     */
    public static Date getStartOfToday() {
        return parseDate(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
    }

    /**
     * 获取今日结束时间
     * @return 结束时间
     */
    public static Date getEndOfToday() {
        return parseDate(LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
    }

    /**
     * 时间戳转化为当天结束的时间戳
     *
     * @param time 时间戳
     * @return yyyyMMddHHmmss String
     */
    public static String getEndOfDay(Long time) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        return yyyyMMddHHmmssFormatter.format(localDateTime.with(LocalTime.MAX));
    }

    public static Date getEndOfDay(LocalDate localDate) {
        return parseDate(LocalDateTime.of(localDate, LocalTime.MAX));
    }

    /**
     * 将时间戳转为字符串
     * @param date  时间戳
     * @return 字符串yyyy-MM-dd HH:mm:ss
     */
    public static String parseLongToString(Long date) {
        return yyyyMMddHHmmssFormatter.format(parseLongToDate(date).toInstant().atZone(ZoneOffset.of("+8")).toLocalDateTime());
    }

    public static String parseDateToString(Date date) {
        return yyyyMMddHHmmssFormatter.format(date.toInstant().atZone(ZoneOffset.of("+8")).toLocalDateTime());
    }

    public static String parseDateToString2(Date date) {
        return MM_DD_YYYY_FORMATTER.format(date.toInstant().atZone(ZoneOffset.of("+8")).toLocalDateTime());
    }

    /**
     * 将date转为字符串
     * @param date date
     * @return 字符串yyyy-MM-dd
     */
    public static String parseToDateString(Date date) {
        return YYYY_MM_DD_FORMATTER.format(date.toInstant().atZone(ZoneOffset.of("+8")).toLocalDate());
    }

    /**
     * 将时间戳转为字符串
     * @param date
     * @param formatter
     * @return 时间字符串
     */
    public static String parseLongToString(Long date, String formatter) {
        return DateTimeFormatter.ofPattern(formatter).format(parseLongToDate(date).toInstant().atZone(ZoneOffset.of("+8")).toLocalDateTime());
    }

    public static Date parseLongToDate(Long time) {
        return new Date(time);
    }

    /**
     * 获取今日星期几
     * @return 星期几
     */
    public static int getWeekOfToday() {
        return LocalDateTime.now().getDayOfWeek().getValue();
    }

    /**
     * 计算今日向前偏移日期的开始时间
     *
     * @param offset     偏移数
     * @param chronoUnit 偏移单位, 例如: 年、月、日、周
     * @return 偏移后的日期
     */
    public static Date caleFrontDateFromTodayWithStart(Integer offset, ChronoUnit chronoUnit) {
        return parseDate(LocalDateTime.now().minus(offset, chronoUnit).with(LocalTime.MIN));
    }

    /**
     * 计算今日向后偏移日期的结束时间
     *
     * @param offset     偏移量
     * @param chronoUnit 偏移单位
     * @return 偏移后的日期
     */
    public static Date caleBehindDateFromTodayWithEnd(Integer offset, ChronoUnit chronoUnit) {
        return parseDate(LocalDateTime.now().plus(offset, chronoUnit).with(LocalTime.MAX));
    }

    /**
     * 计算startDate向后偏移日期
     *
     * @param startDate 起始日期
     * @param offset 偏移量
     * @param chronoUnit 偏移单位
     * @return 偏移后的日期
     */
    public static Date caleBehindDate(Date startDate, Integer offset, ChronoUnit chronoUnit) {
        LocalDate localDate = parseDateToLocal(startDate);
        localDate = localDate.plus(offset, chronoUnit);
        return parseDate(localDate);
    }

    private static LocalDate parseDateToLocal(Date date) {
        return date.toInstant().atZone(ZoneOffset.of("+8")).toLocalDate();
    }

    private static LocalDateTime parseDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneOffset.of("+8")).toLocalDateTime();
    }

    public static Date plusDays(Date startDate, Integer days) {
        LocalDateTime localDateTime = parseDateToLocalDateTime(startDate);
        localDateTime = localDateTime.plusDays(days);
        return parseDate(localDateTime);
    }

    /**
     * 日期加上小时
     * @param startDate 日期
     * @param hours 小时
     * @return 日期加上小时
     */
    public static Date plusHours(Date startDate, Integer hours) {
        LocalDateTime localDateTime = parseDateToLocalDateTime(startDate);
        localDateTime = localDateTime.plusHours(hours);
        return parseDate(localDateTime);
    }

    public static Date plusMinutes(Date startDate, Integer minutes) {
        LocalDateTime localDateTime = parseDateToLocalDateTime(startDate);
        localDateTime = localDateTime.plusMinutes(minutes);
        return parseDate(localDateTime);
    }

    /**
     * 获取本月最大天数
     * @return 最大天数
     */
    public static int getLengthOfMonth() {
        return LocalDate.now().lengthOfMonth();
    }

    /**
     * 获取DayOfMonth
     * @return DayOfMonth
     */
    public static int getDayOfMonth() {
        return LocalDate.now().getDayOfMonth();
    }

    /**
     * 获取MonthDay
     * @return MonthDay
     */
    public static MonthDay getMonthDay() {
        return MonthDay.now();
    }

    /**
     * 获取上个月月天数
     * @return 上个月月天数
     */
    public static int getLastMonthLength() {
        return LocalDate.now().minusMonths(1).lengthOfMonth();
    }

    /**
     * 获取下个月月天数
     * @return 下个月月天数
     */
    public static int getNextMonthLength() {
        return LocalDate.now().plusMonths(1).lengthOfMonth();
    }

    /**
     * 减去日期2023-07-14 23:59:59.999中999毫秒后缀
     * @param date 日期
     * @return 2023-07-14 23:59:59
     */
    public static Date minus999Nanos(Date date) {
        LocalDateTime localDateTime = parseDateToLocalDateTime(date);
        return parseDate(localDateTime.minusNanos(999000000));
    }

    // 3.10
    public static Integer parseDuration(String duration) {
        if (StringUtils.isBlank(duration)) return null;
        String[] splits  = duration.split("\\.");
        int hour = Integer.parseInt(splits[0]);
        int min = Integer.parseInt(splits[1]);
        return hour * 60 + min;
    }

    public static String parseDuration(Long duration) {
        if (duration == null || duration == 0) return "0D 0H 0M";
        int day = (int) (duration/60/24);
        int hour = (int) (duration/60%24);
        int min = (int) (duration%60);
        return day + "D " + hour + "H " + min + "M";
    }


    public static String formatDateYyyyMMdd(Date date) {
        return yyyyMMddFormatter.format(parseDateToLocal(date));
    }

    public static String formatDateYyyyMMddHHmm(Date date) {
        return yyyyMMddHHmmFormatter.format(parseDateToLocalDateTime(date));
    }

    /**
     * 将时间戳转为字符串
     * @param date
     * @return
     */
    public static String parseLongToStringToHHmm(Long date) {
        return yyyyMMddHHmmFormatter.format(parseLongToDate(date).toInstant().atZone(ZoneOffset.of("+8")).toLocalDateTime());
    }

    /**
     * 比较日期1 是否在日期2 之前
     * @param date1 日期1
     * @param date2 日期2
     * @return the comparator value, negative if less, positive if greater 负数小于，正数大于
     */
    public static int compareTo(Date date1, Date date2) {
        LocalDateTime localDateTime1 = parseDateToLocalDateTime(date1);
        LocalDateTime localDateTime2 = parseDateToLocalDateTime(date2);
        return localDateTime1.compareTo(localDateTime2);
    }

    /**
     * 获取当前日期年份
     * @return 当前日期年份
     */
    public static int getYear() {
        return LocalDateTime.now().getYear();
    }

    /**
     * 获取指定日期
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 时
     * @param minute 分
     * @param second 秒
     * @param nanoOfSecond 纳秒
     * @return 日期
     */
    public static Date getDateByYearAndMonthDay(int year, int month, int day, int hour, int minute, int second, int nanoOfSecond) {
        return parseDate(LocalDateTime.of(year, month, day, hour, minute, second, nanoOfSecond));
    }

    /**
     * 获取当前日期的下个月日期
     * @param localDate 当前日期
     * @return 下个月今日的凌晨
     */
    public static LocalDate getNextMonth(LocalDate localDate) {
        return localDate.plusMonths(1);
    }

    public static LocalDate getLastMonth(LocalDate localDate) {
        return localDate.plusMonths(-1);
    }

    public static LocalDate getNextDay(Date date) {
        return parseDateToLocalDateTime(date).plusDays(1).toLocalDate();
    }

    public static Long parseDateToLong(Date date) {
        return parseDateToLocalDateTime(date).toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
    public static String getYyMMddFormat(String flightDate){
        SimpleDateFormat format=new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
        //Locale里面还有其他国家的时间转换方法，如US
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
        Date date= null;
        try {
            //parse String转换成date
            date = format.parse(flightDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String InterNationalTime=format1.format(date);
        return InterNationalTime.toUpperCase();
    }
    public static String getHHmmFormat(String flightDate){
        SimpleDateFormat format=new SimpleDateFormat("HHmm");
        //Locale里面还有其他国家的时间转换方法，如US
        SimpleDateFormat format1=new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Date date= null;
        try {
            //parse String转换成date
            date = format.parse(flightDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String InterNationalTime=format1.format(date);
        return InterNationalTime.toUpperCase();
    }
    /**
     * 获取N天前或者N天后日期
     * num为负时，代表num天前
     * num为正时，代表num天后
     * @param date
     * @param num
     * @return
     */
    public static String getBeforeDay(String date, int num) throws Exception {
        //将时间格式化成yyyy-MM-dd HH:mm:ss的格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = format.parse(date);
        //创建Calendar实例
        Calendar cal = Calendar.getInstance();
        //设置当前时间
        cal.setTime(parse);
        //在当前时间基础上减一年
        cal.add(Calendar.DATE, num);
        //在当前时间基础上减一月
        /*cal.add(Calendar.MONTH,-1);
        System.out.println(format.format(cal.getTime()));
        //同理增加一天的方法：
        cal.add(Calendar.DATE, 1);
        System.out.println(format.format(cal.getTime()));*/

        Date time = cal.getTime();
        return format.format(time);
    }

    /**
     * 将英文缩写月份转为数字
     * @param month
     * @return
     */
    public static int getMonth(String month) {
        // 使用SimpleDateFormat类将英文缩写的月份转换为数字月份
        SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.ENGLISH);
        Date date = new Date();
        try {
            date = sdf.parse(month);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取数字月份
        return date.getMonth() + 1;
    }


    public static String getBetween(String start,String end,DateTimeFormatter dateTimeFormatter){
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm:ss");
        //将String转换为localDateTime类型
        LocalDateTime localDateTime1 = LocalDateTime.parse(start,dateTimeFormatter);
        LocalDateTime localDateTime2 = LocalDateTime.parse(end,dateTimeFormatter);
        //LocalDateTime自带时间差计算（开始时间，结束时间）
        Duration duration = Duration.between(localDateTime1,localDateTime2);
        //两个时间之间，总共差的天/时/分/秒，
        long day = duration.toDays();
        long hours = duration.toHours();
        long minute = duration.toMinutes();
        //toMillis()获取的是毫秒所以除以1000换算成秒
        long second = duration.toMillis()/1000;
        //两个时间之间差了几天又几时又几分又几秒
        long Hours = hours - day * 24;
        long Minute = (minute - day * 24 * 60) - (Hours * 60);
        long Second = (second - day * 24 * 60 * 60) - (Hours * 60 * 60) - (Minute * 60);
        String time="";
        if(day==0L){
            time=Hours+"h"+Minute+"mins";
        }else {
            /*time=day+"天"+
                    Hours+"小时"+
                    Minute+"分钟";*/
            long hs=day*24+Hours;
            time=hs+"h"+
                    Minute+"mins";
        }

        return time;
    }

    /**
     * 从日期中获得星期几。
     *
     * @param date 日期
     * @return 星期几
     */
    public static String getWeekOfDate(Date date){
        LocalDate localDate = parseDateToLocal(date);
        return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINESE);
    }

    public static String addTime(String time1, String time2) {
        if (StringUtils.isBlank(time1)) return time2;
        if (StringUtils.isBlank(time2)) return time1;
        String[] splits = time1.split("\\.");
        String[] splits2 = time2.split("\\.");
        if (splits.length == 2 && splits2.length == 2) {
            LocalTime localTime1 = LocalTime.of(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), 0);
            LocalTime localTime2 = LocalTime.of(Integer.parseInt(splits2[0]), Integer.parseInt(splits2[1]), 0);
            LocalTime sum = localTime1.plusHours(localTime2.getHour()).plusMinutes(localTime2.getMinute());
            return sum.getHour() + "." + sum.getMinute();
        }
        return "";
    }

    /**
     * 根据到达时间，转换到达日期
     * @param arrivalDate   达到日期 2023-12-20
     * @param arrivalTime   到达时间 08:00[+1/-1]
     * @return  真实的到达时间
     */
    public static String convertArrivalDate(String arrivalDate, String arrivalTime) {
        int day = 0;
        if (arrivalTime.contains("+")) {
            day = Integer.parseInt(arrivalTime.split("[+]")[1]);
        }
        if (arrivalTime.contains("-")) {
            day = Integer.parseInt(arrivalTime.split("-")[1]);
        }
        Date date = plusDays(TimeUtils.parseStringToDate(arrivalDate, "yyyy-MM-dd"), day);
        return parseToDateString(date);
    }

    /**
     * 格式化字符串日期到英文日期
     * @param date  日期2023-12-01
     * @return 英文日期 01DEC
     */
    public static String parseStringToEnglishDate(String date) {
        if (StringUtils.isBlank(date)) return "";
        LocalDate localDate = parseDateToLocal(Objects.requireNonNull(parseStringToDate(date, "yyyy-MM-dd")));
        Month month = localDate.getMonth();
        String monthName = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        return localDate.getDayOfMonth()+monthName;
    }

    /**
     * 格式化时间04:05成分钟
     * @param time 时间04:05
     * @return  min
     */
    public static Long parseTimeToMin(String time) {
        String[] times = time.split(":");
        return Long.parseLong(times[1]) + Long.parseLong(times[0]) * 60L;
    }

    /**
     * 计算两个字符串日期格式的差值，转成分钟返回
     * @param arrivalDate
     * @param arrivalTime
     * @return min
     */
    public static Long getBetweenParseTimeToMin(String arrivalDate, String arrivalTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 将字符串转换为 LocalDateTime
        LocalDateTime dateTime1 = LocalDateTime.parse(arrivalDate, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(arrivalTime, formatter);

        // 计算两个日期时间之间的差值（以分钟为单位）
        long minutesDifference = ChronoUnit.MINUTES.between(dateTime1, dateTime2);

        return minutesDifference;

    }

    // 27JAN53-->1953-01-27
    public static String parseEnglishStringToDate(String englishDate) {
        if (StringUtils.isBlank(englishDate)) return null;
        int day = Integer.parseInt(englishDate.substring(0,2));
        String month = englishDate.substring(2,5);
        int year = Integer.parseInt(englishDate.substring(5));

        int thisYear = LocalDate.now().getYear();
        int thisYearSuffix = thisYear%100;
        if (thisYearSuffix >= year) {
            year = year + 2000;
        } else {
            year = year + 1900;
        }

        Month realMonth = null;
        Month[] months = Month.values();
        for (Month month1 : months) {
            if (month1.name().startsWith(month)) {
                realMonth = month1;
                break;
            }
        }
        LocalDate localDate = LocalDate.of(year, realMonth, day);
        return localDate.toString();
    }

    // 21JUL25-->2025-07-21
    public static String parseEnglishStringToDate2(String englishDate) {
        if (StringUtils.isBlank(englishDate)) return null;
        int day = Integer.parseInt(englishDate.substring(0,2));
        String month = englishDate.substring(2,5);
        int year = Integer.parseInt(englishDate.substring(5)) + 2000;
        Month realMonth = null;
        Month[] months = Month.values();
        for (Month month1 : months) {
            if (month1.name().startsWith(month)) {
                realMonth = month1;
                break;
            }
        }
        LocalDate localDate = LocalDate.of(year, realMonth, day);
        return localDate.toString();
    }

    // TU25DEC-->2023-12-25/2024-12-25
    public static String parseEnglishStringToDate3(String date) {
        if (StringUtils.isBlank(date)) return null;
        int day = Integer.parseInt(date.substring(2,4));
        String month = date.substring(4);
        Month realMonth = null;
        Month[] months = Month.values();
        for (Month month1 : months) {
            if (month1.name().startsWith(month)) {
                realMonth = month1;
                break;
            }
        }
        LocalDate now = LocalDate.now();
        Month nowMonth = now.getMonth();
        int dayOfMonth = now.getDayOfMonth();
        if (realMonth.getValue() > nowMonth.getValue()) {
            return LocalDate.of(now.getYear(), realMonth, day).toString();
        } else if (realMonth.getValue() == nowMonth.getValue()){
            if (day >= dayOfMonth) {
                return LocalDate.of(now.getYear(), realMonth, day).toString();
            } else {
                return LocalDate.of(now.plusYears(1L).getYear(), realMonth, day).toString();
            }
        } else {
            return LocalDate.of(now.plusYears(1L).getYear(), realMonth, day).toString();
        }
    }

    // 60min-->1h0mins
    public static String parseHourAndMin(Long time) {
        return time/60 + "h"  + time%60 + "mins";
    }
}
