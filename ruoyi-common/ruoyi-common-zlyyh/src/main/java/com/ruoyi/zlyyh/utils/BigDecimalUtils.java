package com.ruoyi.zlyyh.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 单位转换处理类
 *
 * @author yzgnet
 */
public class BigDecimalUtils {
    /**
     * 四舍五入
     * @param amount 金额 元
     * @return 金额 元
     */
    public static BigDecimal round(BigDecimal amount){
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 元 转 分
     * @param amount 金额 元
     * @return 金额 分
     */
    public static Integer toMinute(BigDecimal amount){
        return amount.multiply(new BigDecimal("100")).intValue();
    }

    /**
     * 分 转 元
     * @param amount 金额 分
     * @return 金额 元
     */
    public static BigDecimal toMoney(Integer amount){
        if(null == amount){
            return new BigDecimal("0");
        }
        return new BigDecimal(amount.toString()).multiply(new BigDecimal("0.01"));
    }
}
