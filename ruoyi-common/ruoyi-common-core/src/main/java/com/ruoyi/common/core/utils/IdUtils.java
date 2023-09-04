package com.ruoyi.common.core.utils;


import cn.hutool.core.lang.UUID;

import java.util.Calendar;
import java.util.Random;

/**
 * ID生成器工具类
 *
 * @author ruoyi
 */
public class IdUtils {

    /**
     * 根据当前时间生成ID
     *
     * @return ID
     */
    public static String getDateUUID() {
        Calendar Cld = Calendar.getInstance();
        int YY = Cld.get(Calendar.YEAR);
        int MM = Cld.get(Calendar.MONTH) + 1;
        int DD = Cld.get(Calendar.DATE);
        int HH = Cld.get(Calendar.HOUR_OF_DAY);
        int mm = Cld.get(Calendar.MINUTE);
        int SS = Cld.get(Calendar.SECOND);
        return YY + format("%02d", MM) + format("%02d", DD) + format("%02d", HH) + format("%02d", mm) + format("%02d", SS) + format("%04d", new Random().nextInt(999999));
    }

    /**
     * 根据当前时间生成长ID
     *
     * @return ID
     */
    public static String getDateLongUUID() {
        return getDateUUID() + format("%05d", new Random().nextInt(99999));
    }

    /**
     * 根据当前时间生成ID
     *
     * @param prefix 在时间ID前面加的其他内容
     * @return ID
     */
    public static String getDateUUID(String prefix) {
        return null == prefix ? getDateUUID() : prefix + getDateUUID();
    }

    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }


    private static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 生成5-32位UUID 建议8位及以上，位数越少，重复的几率越大，例如生成5位的，10万个就会有五个左右重复
     * 建议生成位数为偶数
     *
     * @return UUID
     */
    public static String getShortUUID(int length) {
        if (length > 32) {
            length = 32;
        }
        if (length < 5) {
            length = 5;
        }
        StringBuffer shortBuffer = new StringBuffer();
        //获取32位UUID
        String uuid = fastSimpleUUID();
        for (int i = 0; i < length; i++) {
            // 32位的UUID，每4位处理变成1位，结果变成8位UUID
            // String str = uuid.substring(i * 4, i * 4 + 4);
            // 由以上公式改的
            String str = uuid.substring(i * (uuid.length() / length), (i + 1) * (uuid.length() / length));
            //输出16进制数
            int x = Integer.parseInt(str, 16);
            //3E  转16进制
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }

    /**
     * 数字格式处理
     *
     * @param format 格式
     * @param number 数字
     * @return 处理后字符串
     */
    private static String format(String format, int number) {
        return String.format(format, number);
    }
}
