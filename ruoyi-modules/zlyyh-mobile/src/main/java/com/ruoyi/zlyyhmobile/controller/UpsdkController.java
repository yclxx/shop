package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.properties.utils.YsfPropertiesUtils;
import com.ruoyi.zlyyh.utils.LocationUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyh.utils.sdk.YinLianUtil;
import com.ruoyi.zlyyhmobile.domain.bo.UpsdkInitBo;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/upsdk/ignore")
public class UpsdkController {
    private final String FRONTTOKEN = "frontTokenKey";
    private final String DELETECOUNT = "deleteCount";
    private final String DELECTCOUNTMAX = "deleteCountMax";
    private final IPlatformService platformService;

    /**
     * 初始化upsdk
     *
     * @return 返回初始化所需参数
     */
    @RequestMapping("/upsdkConfigInit")
    public R<HashMap<String, Object>> upsdkConfigInit(@RequestBody UpsdkInitBo upsdkInitBo) {
        log.info("初始化upsdk接收参数：{}", upsdkInitBo);
        if (ObjectUtil.isEmpty(upsdkInitBo.getUrl())) {
            return R.fail("初始化信息失败，请关闭重试");
        }
        String url = upsdkInitBo.getUrl();
        if (url.contains("#")) {
            // 去掉#和后面的字符串
            url = url.substring(0, url.indexOf("#"));
        }
        // 平台ID
        Long platformId = ZlyyhUtils.getPlatformId();
        if (ObjectUtil.isEmpty(platformId)) {
            log.info("未获取到平台ID,{}", ZlyyhUtils.getPlatformId());
            return R.fail("初始化信息失败，请关闭重试");
        }
        PlatformVo platformVo = platformService.queryById(platformId, ZlyyhUtils.getPlatformChannel());
        if (ObjectUtil.isNull(platformVo)) {
            return R.fail("请求错误！");
        }
        url = url.replace("&amp;", "&");
        // 生成随机串
        String nonceStr = YinLianUtil.createNonceStr();
        // 生成时间戳，秒
        String timestamp = YinLianUtil.createTimestamp();
        // 获取前端基础访问令牌
        String frontToken = getFrontToken(platformVo.getAppId(), platformVo.getSecret(), platformVo.getPlatformKey());
        if (ObjectUtil.isEmpty(frontToken)) {
            return R.fail("初始化信息失败，请关闭重试");
        }
        HashMap<String, String> sdkMap = new HashMap<>();
        sdkMap.put("appId", platformVo.getAppId());
        sdkMap.put("nonceStr", nonceStr);
        sdkMap.put("timestamp", timestamp);
        sdkMap.put("url", url);
        sdkMap.put("frontToken", frontToken);

        log.info("初始化upsdk签名参数：{}", sdkMap);
        // 以键值对的形式拼接成字符串
        String sdkStr = StringUtils.joinToLinkString(sdkMap);
        // sha256签名
        String sdkSignature = YinLianUtil.sha256(sdkStr.getBytes());
        HashMap<String, Object> map = new HashMap<>();
        map.put("appId", platformVo.getAppId());
        map.put("timestamp", timestamp);
        map.put("nonceStr", nonceStr);
        map.put("signature", sdkSignature);

        log.info("初始化upsdk返回给前端参数：{}", map);
        return R.ok(map);
    }

    /**
     * 删除redis中的frontToken
     *
     * @return 返回结果
     */
    @RequestMapping("/deleteFrontToken")
    public R<Void> deleteFrontToken() {
        // 平台ID
        Long platformId = ZlyyhUtils.getPlatformId();
        if (ObjectUtil.isEmpty(platformId)) {
            return R.fail("删除失败");
        }
        PlatformVo platformVo = platformService.queryById(platformId, ZlyyhUtils.getPlatformChannel());
        if (ObjectUtil.isNull(platformVo)) {
            return R.fail("请求错误！");
        }
        Integer count = RedisUtils.getCacheObject(DELETECOUNT + platformVo.getAppId());
        if (ObjectUtil.isNull(count)) {
            RedisUtils.setCacheObject(DELETECOUNT + platformVo.getAppId(), 1, Duration.ofSeconds(60 * 60));
            return R.ok();
        } else {
            int countMax = 10;
            Integer strCount = RedisUtils.getCacheObject(DELECTCOUNTMAX + platformVo.getAppId());
            if (ObjectUtil.isNotEmpty(strCount)) {
                countMax = strCount;
            } else {
                RedisUtils.setCacheObject(DELECTCOUNTMAX + platformVo.getAppId(), countMax, Duration.ofSeconds(60 * 60));
            }
            if (count >= countMax) {
                RedisUtils.deleteObject(FRONTTOKEN + platformVo.getAppId());
                RedisUtils.deleteObject(DELETECOUNT + platformVo.getAppId());
                log.info("删除redis中缓存的frontToken");
                return R.ok();
            } else {
                count = count + 1;
                RedisUtils.setCacheObject(DELETECOUNT + platformVo.getAppId(), count, Duration.ofSeconds(60 * 60));
                return R.ok();
            }
        }
    }

    /**
     * 获取商品列表
     *
     * @return 商品列表
     */
    @GetMapping("/getLocationCity")
    public R<Map<String, String>> getLocationCity(String location) {
        Map<String, String> locationCity = LocationUtils.getLocationCity(location);
        return R.ok(locationCity);
    }

    /**
     * 基础服务令牌，通过appId、secret换取，有效期为7200秒，upsdk初始化凭证。
     *
     * @return 返回结果
     */
    private String getFrontToken(String appid, String secret, Long platformKey) {
        String s = RedisUtils.getCacheObject(FRONTTOKEN + appid);
        if (StringUtils.isNotEmpty(s)) {
            return s;
        }
        // 生成随机串
        String nonceStr = YinLianUtil.createNonceStr();
        // 生成时间戳，秒
        String timestamp = YinLianUtil.createTimestamp();

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appId", appid);
        paramMap.put("secret", secret);
        paramMap.put("nonceStr", nonceStr);
        paramMap.put("timestamp", timestamp);
        // 以键值对的形式拼接成字符串
        String str = StringUtils.joinToLinkString(paramMap);
        // 通过SHA256进行签名
        String signature = YinLianUtil.sha256(str.getBytes());
        // 密钥无需请求
        paramMap.remove("secret");
        paramMap.put("signature", signature);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(YsfPropertiesUtils.getGetFrontToken(platformKey), JSONObject.toJSONString(paramMap));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        String resp = jsonObject.getString("resp");
        String resultParams = jsonObject.getString("params");
        if ("00".equals(resp)) {
            JSONObject resultObject = JSONObject.parseObject(resultParams);
            String frontToken = resultObject.getString("frontToken");
            Integer expiresIn = resultObject.getInteger("expiresIn");
            if (null == expiresIn || expiresIn <= 0) {
                // 如果没有时间，默认一小时
                expiresIn = 60 * 60;
            } else {
                expiresIn = new Double(expiresIn * 0.7).intValue();
            }
            RedisUtils.setCacheObject(FRONTTOKEN + appid, frontToken, Duration.ofSeconds(expiresIn));
            return frontToken;
        } else {
            return null;
        }
    }
}
