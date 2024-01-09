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
 * 百度智能云工具类
 */
@Slf4j
public class BaiduUtils {

    public static String ocrComm(String imgUrl, String accessToken) {
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate?access_token=" + accessToken;
        url = url + "&url=" + imgUrl;
        String post = HttpUtil.post(url, "");
        log.info("百度ocr识别，请求信息：{}，返回结果：{}", url, post);
        return post;
    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     */
    public static String getAccessToken(String apiKey, String secretKey) {
        // 获取基础访问令牌
        String accessTokenRedisKey = "accessToken:" + apiKey;
        String accessToken = RedisUtils.getCacheObject(accessTokenRedisKey);
        if (StringUtils.isNotEmpty(accessToken)) {
            return accessToken;
        }
        LockTemplate lockTemplate = SpringUtils.getBean(LockTemplate.class);
        final LockInfo lockInfo = lockTemplate.lock(apiKey, 30000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            log.error("获取百度基础访问令牌失败，已有锁在执行");
            return null;
        }
        // 获取锁成功，处理业务
        try {
            // 请求接口获取访问令牌
            String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + secretKey;
            String result = HttpUtil.post(url, "");
            log.info("获取百度基础访问令牌请求url:{},返回结果:{}", url, result);
            Dict dict = JsonUtils.parseMap(result);
            if (null == dict) {
                log.error("获取百度基础访问令牌失败，调用接口返回结果为空");
                return null;
            }
            accessToken = dict.getStr("access_token");
            if (StringUtils.isEmpty(accessToken)) {
                log.error("获取百度基础访问令牌失败，无令牌，返回信息：{}", result);
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
    }
}
