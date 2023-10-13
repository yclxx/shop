package com.ruoyi.zlyyh.constant;

/**
 * 通用常量信息
 *
 * @author Lion Li
 */
public interface ZlyyhConstants {
    /**
     * 云闪付基础访问令牌缓存key
     */
    String BACKEND_TOKEN_REDIS_KEY = "BackendToken";
    /**
     * 用户真实定位城市，主要用于需要校验用户位置才能参与活动的需求
     */
    String AD_CODE = "adcode";
    /**
     * 用户真实定位城市名称
     */
    String CITY_NAME = "cityname";
    /**
     * 用户选择的城市，适用于根据城市查询对应数据
     */
    String CITY_CODE = "citycode";
    /**
     * 渠道
     */
    String PLATFORM_TYPE = "platformType";
    /**
     * 门店缓存key
     */
    String SHOP_GEO_CACHE_KEY = "shopGeoCacheKey";

    String SEND_COUPON_MQ_COUNT = "sendCouponMqCount";
    /**
     * 订单发券次数缓存
     */
    String SEND_COUPON_NUMBER = "OrderSendCouponNumber";
    /**
     * 订单失败次数缓存
     */
    String ysfOrderErrorNum = "ysfOrderErrorNum";
    /**
     * 邀请新用户判定时间天数
     */
    String inviteUserDay = "inviteUserDay";
}
