package com.ruoyi.common.core.constant;

/**
 * 通用常量信息
 *
 * @author Lion Li
 */
public interface Constants {
    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    String GBK = "GBK";

    /**
     * www主域
     */
    String WWW = "www.";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    Integer FAIL = 500;

    /**
     * 登录成功状态
     */
    String LOGIN_SUCCESS_STATUS = "0";

    /**
     * 登录失败状态
     */
    String LOGIN_FAIL_STATUS = "1";

    /**
     * 登录成功
     */
    String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    String LOGOUT = "Logout";

    /**
     * 注册
     */
    String REGISTER = "Register";

    /**
     * 登录失败
     */
    String LOGIN_FAIL = "Error";

    /**
     * 验证码有效期（分钟）
     */
    long CAPTCHA_EXPIRATION = 2;

    /**
     * 防重提交 redis key
     */
    String REPEAT_SUBMIT_KEY = "repeat_submit:";

    String ACCESS_TOKEN = "access_token";
    /**
     * 平台key 传递标识
     */
    String PLATFORM_KEY = "platformKey";
    /**
     * 平台key 传递标识
     */
    String DISTRICT_AD_CODE = "adcode";

    /**
     * 资源映射路径 前缀
     */
//    String RESOURCE_PREFIX = "/profile";
    String RESOURCE_PREFIX = "/resource";

    /**
     * 渠道
     */
    String PLATFORM_TYPE = "platformType";
    /**
     * 分销用户ID
     */
    String SHARE_USER_ID = "shareUserId";
}
