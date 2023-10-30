package com.ruoyi.zlyyh.utils;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.AESUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.properties.CloudRechargeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 充值中心帮助类
 *
 * @author 25487
 */
@Slf4j
public class CloudRechargeUtils {
    private static CloudRechargeConfig cloudrechargeConfig = SpringUtils.getBean(CloudRechargeConfig.class);

    /**
     * 签名
     *
     * @return sign
     */
    public static String sign(CloudRechargeEntity cloudRechargeEntity, String sercret) {
        String str = cloudRechargeEntity.getAppId() + cloudRechargeEntity.getTimestamp() + cloudRechargeEntity.getEncryptedData() + sercret;
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8)).toUpperCase();
    }

    /**
     * 验证签名
     *
     * @return 验签通过true，失败false
     */
    private static boolean verifySign(CloudRechargeEntity cloudRechargeEntity, String sercret) {
        return sign(cloudRechargeEntity, sercret).equals(cloudRechargeEntity.getSign());
    }

    /**
     * 发送请求
     *
     * @param appId        应用IDID
     * @param data         请求数据
     * @param secret       密钥
     * @param symmetricKey 对称密钥
     * @param url          请求地址
     * @return 返回解密后结果
     */
    private static CloudRechargeResult doPostHuiguyun(String appId, Object data, String secret, String symmetricKey, String url) {
        CloudRechargeEntity entity = new CloudRechargeEntity();
        entity.setAppId(appId);
        entity.setTimestamp(String.valueOf(System.currentTimeMillis()));
        String encryptedData;
        try {
            String dataStr = JsonUtils.toJsonString(data);
            if (StringUtils.isEmpty(dataStr)) {
                dataStr = "";
            }
            encryptedData = AESUtils.encrypt(symmetricKey, dataStr, Constants.UTF8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("云充值中心接口对称加密异常，请求URL：" + url);
        }
        entity.setEncryptedData(encryptedData);
        entity.setSign(sign(entity, secret));
        String params = JsonUtils.toJsonString(entity);
        String s;
        try {
            s = HttpUtil.createPost(url)
                .body(params)
                .execute().body();
        } catch (HttpException e) {
            log.error("云充值中心请求链接：" + url + "参数：" + params + "异常结果：" + e.getMessage(), e);
            throw new ServiceException("请求异常");
        }
        CloudRechargeResult huiguyunResult;
        try {
            huiguyunResult = JsonUtils.parseObject(s, CloudRechargeResult.class);
        } catch (Exception e) {
            log.error("Json转换异常：", e);
            throw new ServiceException("JSON转换异常");
        }
        if (null == huiguyunResult) {
            throw new ServiceException("无返回结果");
        }
        if (huiguyunResult.getCode() != 200) {
            return huiguyunResult;
        }
        if (StringUtils.isEmpty(huiguyunResult.getData())) {
            throw new ServiceException("请求返回无数据");
        }
        // 解密
        if (StringUtils.isNotEmpty(huiguyunResult.getData())) {
            try {
                huiguyunResult.setData(AESUtils.decrypt(symmetricKey, huiguyunResult.getData(), Constants.UTF8));
            } catch (Exception e) {
                throw new ServiceException("云充值中心接口对称解密异常，请求URL：" + url);
            }
        }
        log.info("云充值中心请求链接：" + url + "参数：" + params + "结果：" + s);
        return huiguyunResult;
    }

    /**
     * 下单
     */
    public static R<Void> doPostCreateOrder(Long number, String productId, String account, Integer count, String pushNumber, String replaceCallback) {
        Map<String, Object> data = new HashMap<>();
        data.put("productId", productId);
        data.put("count", count);
        data.put("account", account);
        data.put("externalOrderNumber", pushNumber);
        if (StringUtils.isBlank(replaceCallback)) {
            data.put("orderBack", cloudrechargeConfig.getCallbackUrl());
        } else {
            if (StringUtils.isNotBlank(cloudrechargeConfig.getCallbackUrl())) {
                data.put("orderBack", cloudrechargeConfig.getCallbackUrl().replace("/zlyyh-mobile/order/ignore/orderCallback", replaceCallback));
            }
        }
        CloudRechargeResult huiguyunResult;
        try {
            huiguyunResult = CloudRechargeUtils.doPostHuiguyun(cloudrechargeConfig.getAppId(), data, cloudrechargeConfig.getAppKey(), cloudrechargeConfig.getAesKey(), cloudrechargeConfig.getSubmitUrl());
        } catch (Exception e) {
            log.error("充值中心请求异常：", e);
            return R.warn("充值中心请求异常：" + e.getMessage());
        }
        log.info("订单：{}，充值中心请求url:{},参数：{},响应：{}", number, cloudrechargeConfig.getSubmitUrl(), data, huiguyunResult);
        if (huiguyunResult.getCode() != 200) {
            if (huiguyunResult.getCode() == 1012 || huiguyunResult.getCode() == 1011) {
                return R.warn(huiguyunResult.getMsg());
            }
            return R.fail("充值中心请求失败：" + huiguyunResult.getMsg());
        }
        return R.warn("请求成功");
    }

    /**
     * 查询订单
     */
    public static R<JSONObject> doPostQueryOrder(Long number, String pushNumber) {
        Map<String, Object> data = new HashMap<>();
        data.put("externalOrderNumber", pushNumber);
        CloudRechargeResult huiguyunResult;
        try {
            huiguyunResult = CloudRechargeUtils.doPostHuiguyun(cloudrechargeConfig.getAppId(), data, cloudrechargeConfig.getAppKey(), cloudrechargeConfig.getAesKey(), cloudrechargeConfig.getQueryUrl());
        } catch (Exception e) {
            return R.warn("请求异常：" + e.getMessage());
        }
        log.info("订单：{}，充值中心请求url:{},参数：{},响应：{}", number, cloudrechargeConfig.getSubmitUrl(), data, huiguyunResult);
        if (huiguyunResult.getCode() != 200) {
            if (huiguyunResult.getCode() == 500) {
                return R.warn("请求异常：" + huiguyunResult.getMsg());
            }
            if (huiguyunResult.getCode() == 1017) {
                return R.fail(JSONObject.parseObject(huiguyunResult.getData()));
            }
            return R.warn("订单不存在");
        }
        return R.ok(JSONObject.parseObject(huiguyunResult.getData()));
    }

    /**
     * 验签，并解密
     *
     * @param cloudRechargeEntity 回调参数
     */
    public static void getData(CloudRechargeEntity cloudRechargeEntity) {
        boolean b = verifySign(cloudRechargeEntity, cloudrechargeConfig.getAppKey());
        if (!b) {
            log.error("云充值中心通知验签失败,参数：" + cloudRechargeEntity);
            throw new ServiceException("验签失败");
        }
        // 解密
        try {
            String decrypt = AESUtils.decrypt(cloudrechargeConfig.getAesKey(), cloudRechargeEntity.getEncryptedData(), Constants.UTF8);
            cloudRechargeEntity.setEncryptedData(decrypt);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("云充值中心通知解密异常：", e);
            throw new ServiceException("解密异常");
        }
        if (StringUtils.isEmpty(cloudRechargeEntity.getEncryptedData())) {
            log.error("云充值中心通知内容为空");
            throw new ServiceException("通知内容为空");
        }
        log.info("云充值中心通知内容{}", cloudRechargeEntity);
    }
}
