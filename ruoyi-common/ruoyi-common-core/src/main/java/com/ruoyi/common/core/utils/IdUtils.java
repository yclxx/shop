package com.ruoyi.common.core.utils;

import cn.hutool.core.util.IdUtil;

/**
 * ID生成器工具类
 *
 * @author ruoyi
 */
public class IdUtils {

    /**
     * 根据当前时间生成ID
     *
     * @param prefix 在时间ID前面加的其他内容
     * @return ID
     */
    public static String getSnowflakeNextIdStr(String prefix) {
        return null == prefix ? IdUtil.getSnowflakeNextIdStr() : prefix + IdUtil.getSnowflakeNextIdStr();
    }

    public static String getSortNumbers(){
        String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
        return snowflakeNextIdStr.substring(9);
    }
}
