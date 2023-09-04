package com.ruoyi.zlyyhmobile.utils.redis;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.OrderInfo;
import com.ruoyi.zlyyh.domain.OrderPushInfo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单缓存帮助类
 *
 * @author 25487
 */
public class OrderCacheUtils {

    /**
     * 设置订单发券缓存
     *
     * @param pushInfo 订单发券信息
     */
    public static void setOrderPushCache(OrderPushInfo pushInfo) {
        String userName = null;
        try {
            userName = LoginHelper.getUsername();
        } catch (Exception ignored) {
        }
        // 判断是否有缓存过
        boolean existsObject = RedisUtils.isExistsObject(getOrderPushCacheKey(pushInfo.getPushNumber()));
        if (!existsObject) {
            pushInfo.setCreateBy(userName);
            pushInfo.setCreateTime(new Date());
            // 设置订单发券记录
            RedisUtils.setCacheList(getOrderPushListCacheKey(pushInfo.getNumber()), pushInfo.getPushNumber());
        }
        pushInfo.setUpdateBy(userName);
        pushInfo.setUpdateTime(new Date());
        // 设置订单缓存
        RedisUtils.setCacheObject(getOrderPushCacheKey(pushInfo.getPushNumber()), pushInfo);
    }

    /**
     * 获取订单发券缓存
     *
     * @param pushNumber 发券订单号
     */
    public static OrderPushInfo getOrderPushCache(String pushNumber) {
        // 获取订单缓存
        return RedisUtils.getCacheObject(getOrderPushCacheKey(pushNumber));
    }

    /**
     * 设置订单缓存
     *
     * @param order 订单信息
     */
    public static void setOrderCache(Order order) {
        String userName = null;
        try {
            userName = LoginHelper.getUsername();
        } catch (Exception ignored) {
        }
        // 判断是否有缓存过
        boolean existsObject = RedisUtils.isExistsObject(getOrderCacheKey(order.getNumber()));
        if (!existsObject) {
            order.setCreateBy(userName);
            order.setCreateTime(new Date());
            // 设置需要保存数据库的订单号
            RedisUtils.setCacheList(getOrderCacheKey(), order.getNumber());
            // 设置用户在缓存中的订单
            RedisUtils.setCacheList(getUserOrderCacheKey(order.getUserId()), order.getNumber());
        }
        order.setUpdateBy(userName);
        order.setUpdateTime(new Date());
        // 设置订单缓存
        RedisUtils.setCacheObject(getOrderCacheKey(order.getNumber()), order);
    }

    /**
     * 设置订单详情缓存
     *
     * @param orderInfo 订单详情信息
     */
    public static void setOrderInfoCache(OrderInfo orderInfo) {
        String userName = null;
        try {
            userName = LoginHelper.getUsername();
        } catch (Exception ignored) {
        }
        // 判断是否有缓存过
        boolean existsObject = RedisUtils.isExistsObject(getOrderInfoCacheKey(orderInfo.getNumber()));
        if (!existsObject) {
            orderInfo.setCreateBy(userName);
            orderInfo.setCreateTime(new Date());
        }
        orderInfo.setUpdateBy(userName);
        orderInfo.setUpdateTime(new Date());
        // 设置订单缓存
        RedisUtils.setCacheObject(getOrderInfoCacheKey(orderInfo.getNumber()), orderInfo);
    }

    /**
     * 删除订单对应缓存
     *
     * @param number 订单号
     * @param userId 用户ID
     */
    public static void delOrderCache(Long number, Long userId) {
        RedisUtils.deleteObject(getOrderCacheKey(number));
        RedisUtils.delCacheList(getOrderCacheKey(), number);
        if(null != userId){
            RedisUtils.delCacheList(getUserOrderCacheKey(userId), number);
        }
        List<String> pushNumbers = RedisUtils.getCacheList(getOrderPushListCacheKey(number));
        if (ObjectUtil.isNotEmpty(pushNumbers)) {
            for (String pushNumber : pushNumbers) {
                RedisUtils.deleteObject(getOrderPushCacheKey(pushNumber));
            }
        }
        RedisUtils.deleteObject(getOrderPushListCacheKey(number));
        RedisUtils.deleteObject(getOrderInfoCacheKey(number));
    }

    /**
     * 获取订单信息
     *
     * @param number 订单号
     */
    public static Order getOrderCache(Long number) {
        return RedisUtils.getCacheObject(getOrderCacheKey(number));
    }

    /**
     * 获取订单信息
     *
     * @param number 订单号
     */
    public static OrderInfo getOrderInfoCache(Long number) {
        return RedisUtils.getCacheObject(getOrderInfoCacheKey(number));
    }

    /**
     * 获取需要保存数据库的订单列表
     */
    public static List<Long> getOrderCache(int start,int end) {
        return RedisUtils.getCacheList(getOrderCacheKey(),start,end);
    }

    /**
     * 获取订单发券记录
     */
    public static List<String> getOrderPushListCache(Long number) {
        return RedisUtils.getCacheList(getOrderPushListCacheKey(number));
    }

    /**
     * 获取用户缓存订单列表
     */
    public static List<OrderVo> getUserOrderCache(Long userId) {
        List<Long> cacheList = RedisUtils.getCacheList(getUserOrderCacheKey(userId));
        if (ObjectUtil.isEmpty(cacheList)) {
            return new ArrayList<>();
        }
        List<OrderVo> orderVos = new ArrayList<>(cacheList.size());
        for (Long number : cacheList) {
            Order orderCache = getOrderCache(number);
            if (null == orderCache) {
                continue;
            }
            orderVos.add(BeanCopyUtils.copy(orderCache, OrderVo.class));
        }
        return orderVos;
    }

    /**
     * 获取订单缓存key
     *
     * @param number 订单号
     * @return 缓存key
     */
    private static String getOrderCacheKey(Long number) {
        return "orderCache:order:" + number;
    }

    /**
     * 获取订单缓存key
     *
     * @param number 订单号
     * @return 缓存key
     */
    private static String getOrderInfoCacheKey(Long number) {
        return "orderCache:orderInfo:" + number;
    }

    /**
     * 获取需要存数据库的redisKey
     *
     * @return 缓存key
     */
    private static String getOrderCacheKey() {
        return "orderCache:mysql";
    }

    /**
     * 获取用户订单redisKey
     *
     * @return 缓存key
     */
    private static String getUserOrderCacheKey(Long userId) {
        return "orderCache:user:" + userId;
    }

    /**
     * 获取订单发券redisKey
     *
     * @return 缓存key
     */
    private static String getOrderPushCacheKey(String pushNumber) {
        return "orderCache:push:" + pushNumber;
    }

    /**
     * 获取订单发券集合redisKey
     *
     * @return 缓存key
     */
    private static String getOrderPushListCacheKey(Long number) {
        return "orderCache:pushList:" + number;
    }

    /**
     * 获取用户未支付订单redisKey
     *
     * @param platformKey 平台标识
     * @param userId      用户ID
     * @param productId   产品ID
     * @return 缓存key
     */
    public static String getUsreOrderOneCacheKey(Long platformKey, Long userId, Long productId) {
        return "userOrders:" + platformKey + ":" + userId + ":" + productId;
    }
}
