package com.ruoyi.zlyyh.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.http.HttpUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * 微信帮助类
 *
 * @author 25487
 * @date 20231017
 */
@Slf4j
public class WxUtils {

    /**
     * 获取微信接口基础访问令牌
     *
     * @param appId  AppID
     * @param secret 密钥
     * @return 基础访问令牌
     */
    public static String getAccessToken(String appId, String secret,String url) {
        // 获取基础访问令牌
        String accessTokenRedisKey = "accessToken:" + appId;
        String accessToken = RedisUtils.getCacheObject(accessTokenRedisKey);
        if (StringUtils.isNotEmpty(accessToken)) {
            return accessToken;
        }
        LockTemplate lockTemplate = SpringUtils.getBean(LockTemplate.class);
        final LockInfo lockInfo = lockTemplate.lock(appId, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            log.error("获取微信基础访问令牌失败，已有锁在执行");
            return null;
        }
        // 获取锁成功，处理业务
        try {
            // 请求接口获取访问令牌
            url = url + "?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
            String result = HttpUtil.get(url);
            log.info("获取微信基础访问令牌请求url:{},返回结果:{}", url, result);
            Dict dict = JsonUtils.parseMap(result);
            if (null == dict) {
                log.error("获取微信基础访问令牌失败，调用接口返回结果为空");
                return null;
            }
            accessToken = dict.getStr("access_token");
            if (StringUtils.isEmpty(accessToken)) {
                log.error("获取微信基础访问令牌失败，无令牌，返回信息：{}", result);
                return null;
            }
            Integer expires_in = dict.getInt("expires_in");
            if (null == expires_in || expires_in < 1) {
                expires_in = 7200;
            }
            int expires = new Double(expires_in * 0.7).intValue();
            // 缓存
            RedisUtils.setCacheObject(accessTokenRedisKey, accessToken, Duration.ofSeconds(expires));
            return accessToken;
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
        //结束
    }
}
