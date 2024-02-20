package com.ruoyi.common.core.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author ruoyi
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String YYYY = "yyyy";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] PARSE_PATTERNS = {
        "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
        "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
        "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate(String patter) {
        return dateTimeNow(patter);
    }

    public static String getMonth() {
        return dateTimeNow(YYYY_MM);
    }

    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), PARSE_PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 计算两个时间差 返回小时差
     */
    public static long getDatePoorHour(Date endDate, Date nowDate) {
        long nh = 1000 * 60 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少小时
        return diff / nh;
    }

    /**
     * 计算两个时间差 返回分钟差
     */
    public static long getDatePoorMinutes(Date endDate, Date nowDate) {
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少小时
        return diff / nm;
    }

    /**
     * 计算两个时间差 返回天差
     */
    public static long getDatePoorDay(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少小时
        return diff / nd;
    }

    /**
     * 计算两个时间差 返回天差
     */
    public static long getDatePoorDay(Date endDate, Date nowDate, boolean day) {
        if (day) {
            return getDatePoorDay(DateUtil.beginOfDay(endDate).toJdkDate(), DateUtil.beginOfDay(nowDate).toJdkDate());
        }
        return getDatePoorDay(endDate, nowDate);
    }

    public static void main(String[] args) {
        Date nowDate = getNowDate();
        Date secondEndTime = getSecondEndTime(nowDate);
        System.out.println(secondEndTime);
//        System.out.println(DateUtils.compare(nowDate,yesterday));
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 获取当前时间戳
     *
     * @param isSecond 是否精确到秒
     * @return isSecond true 返回10位时间戳（秒） false 返回13位时间戳（毫秒）
     */
    public static String createTimestampStr(boolean isSecond) {
        long timeMillis = System.currentTimeMillis();
        if (isSecond) {
            timeMillis = timeMillis / 1000;
        }
        return "" + timeMillis;
    }

    /**
     * 获取当前时间戳
     *
     * @param isSecond 是否精确到秒
     * @return isSecond true 返回10位时间戳（秒） false 返回13位时间戳（毫秒）
     */
    public static long createTimestamp(boolean isSecond) {
        long timeMillis = System.currentTimeMillis();
        if (isSecond) {
            timeMillis = timeMillis / 1000;
        }
        return timeMillis;
    }

    /**
     * 是否大于当前时间
     *
     * @param date 需要比较的时间
     * @return -1 date < 当前时间
     * 0 date = 当前时间
     * 1 date > 当前时间
     */
    public static int compare(Date date) {
        return compare(date, new Date());
    }

    /**
     * 是否大于当前时间
     *
     * @param date1 需要比较的时间
     * @param date2 需要比较的时间
     * @return -1 date1 < date2
     * 0 date1 = date2
     * 1 date1 > date2
     */
    public static int compare(Date date1, Date date2) {
        long between = DateUtil.between(date2, date1, DateUnit.MS, false);
        if (between == 0) {
            return 0;
        }
        if (between > 0) {
            return 1;
        }
        return -1;
    }

    public static String getThisWeekDate(String dayTime) {
        Date thisWeek = getThisWeek(new Date(), dayTime);
        return parseDateToStr(YYYY_MM_DD, thisWeek);
    }

    /**
     * 获取本周的时间
     */
    public static Date getThisWeek(Date date, String dayTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //获取当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        cal.setTime(cal.getTime());
        if ("周一".equals(dayTime)) {
            cal.add(Calendar.DATE, 0);
        } else if ("周二".equals(dayTime)) {
            cal.add(Calendar.DATE, 1);
        } else if ("周三".equals(dayTime)) {
            cal.add(Calendar.DATE, 2);
        } else if ("周四".equals(dayTime)) {
            cal.add(Calendar.DATE, 3);
        } else if ("周五".equals(dayTime)) {
            cal.add(Calendar.DATE, 4);
        } else if ("周六".equals(dayTime)) {
            cal.add(Calendar.DATE, 5);
        } else if ("周日".equals(dayTime)) {
            cal.add(Calendar.DATE, 6);
        }
        return cal.getTime();
    }

    /**
     * 时间戳转时间
     *
     * @param times 时间戳
     * @return 时间
     */
    public static Date TimesToDate(String times) {
        if (ObjectUtil.isEmpty(times)) {
            return null;
        }
        Long timestamp = Long.valueOf(times);
        return new Date(timestamp);
    }

    /**
     * 判断当前时间是否超过开始时间多少毫秒
     *
     * @param begin       开始时间
     * @param millisecond 毫秒
     * @return 超过返回true，没超过返回false
     */
    public static boolean validTime(Date begin, long millisecond) {
        return validTime(begin, new Date(), millisecond);
    }

    /**
     * 判断结束时间是否超过开始时间多少毫秒
     *
     * @param begin       开始时间
     * @param end         结束时间
     * @param millisecond 毫秒
     * @return 超过返回true，没超过返回false
     */
    public static boolean validTime(Date begin, Date end, long millisecond) {
        LocalDateTime beginTimetime = dateToLocalDateTime(begin);
        LocalDateTime endTimetime = dateToLocalDateTime(end);
        if (null == beginTimetime || null == endTimetime) {
            return false;
        }
        Duration duration = Duration.between(beginTimetime, endTimetime);
        long millis = duration.toMillis();//相差毫秒数
        return millis >= millisecond;
    }

    /**
     * Date转LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    private static LocalDateTime dateToLocalDateTime(Date date) {
        try {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取第二天结束时间
     * @param date
     * @return
     */
    public static Date getSecondEndTime(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        LocalDateTime localDateTime = dateTime.plusDays(1).withHour(23).withMinute(59).withSecond(59);
        Date secondTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date secondEndTime = DateUtil.endOfDay(secondTime).offset(DateField.MILLISECOND, -999);
        return secondEndTime;
    }
}
