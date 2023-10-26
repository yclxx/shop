package com.ruoyi.zlyyh.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DESede;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.vo.BackendTokenEntity;
import com.ruoyi.zlyyh.domain.vo.MemberVipBalanceVo;
import com.ruoyi.zlyyh.properties.utils.YsfPropertiesUtils;
import com.ruoyi.zlyyh.utils.sdk.YinLianUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.*;

/**
 * 云闪付相关接口帮助类
 *
 * @author 25487
 */
@Slf4j
public class YsfUtils {

    /**
     * 云闪付任务体系 任务进度查询
     *
     * @param openId 用户openId
     * @return 返回结果
     */
    public static R<Integer> queryMission(String missionId, String openId, String appId, String secret, Long platformKey) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("appId", appId);
            params.put("backendToken", getBackendToken(appId, secret, false, platformKey));
            params.put("openId", openId);
            params.put("missionId", missionId);
            String s = HttpUtil.post(YsfPropertiesUtils.getQueryMissionUrl(platformKey), JSONObject.toJSONString(params));
            log.info("云闪付任务进度查询请求url：{}，请求参数：{}，返回结果：{}", YsfPropertiesUtils.getQueryMissionUrl(platformKey), params, s);
            // {"resp":"00","msg":"成功","params":{"missionProgresses":[{"missionId":"2023051605","missionGroup":"1023051601","missionCycleCompleteTimes":2}],"respMsg":"成功","respCode":"00"}}
            JSONObject resultJson = JSONObject.parseObject(s);
            if ("00".equals(resultJson.getString("resp"))) {
                JSONObject data = resultJson.getJSONObject("params");
                if (null == data) {
                    return R.fail(s);
                }
                JSONArray missionProgresses = data.getJSONArray("missionProgresses");
                if (ObjectUtil.isEmpty(missionProgresses)) {
                    return R.fail(s);
                }
                JSONObject mission = missionProgresses.getJSONObject(0);
                return R.ok(Optional.ofNullable(mission.getInteger("missionCycleCompleteTimes")).orElse(0));
            }
            return R.fail(resultJson.getString("msg"));
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 云闪付任务体系 任务报名
     *
     * @param openId 用户openId
     * @return 返回结果
     */
    public static R<Void> registerMission(String openId, String appId, String secret, Long platformKey) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("backendToken", getBackendToken(appId, secret, false, platformKey));
        params.put("openId", openId);
        params.put("bizTp", YsfPropertiesUtils.getBizTp(platformKey));
        params.put("subBizTp", YsfPropertiesUtils.getSubBizTp(platformKey));
        String s = HttpUtil.post(YsfPropertiesUtils.getRegisterMissionUrl(platformKey), JSONObject.toJSONString(params));
        log.info("云闪付任务报名请求url：{}，请求参数：{}，返回结果：{}", YsfPropertiesUtils.getRegisterMissionUrl(platformKey), params, s);
        // {"resp":"00","msg":"成功","params":{"respMsg":"成功","respCode":"00"}}
        JSONObject resultJson = JSONObject.parseObject(s);
        if ("00".equals(resultJson.getString("resp"))) {
            return R.ok(s);
        }
        return R.fail(resultJson.getString("msg"));
    }

    /**
     * 赠送专享红包
     */
    public static R<Void> sendAcquire(Long number, String pushNumber, String account, String accountType, String externalProductId, BigDecimal amount, String transDigest, Long drawId, Long platformKey) {
        //查询相关参数
        String transTs = DateUtils.dateTimeNow();
        String nonceStr = IdUtil.fastSimpleUUID().substring(0, 12);
        String timestamp = DateUtils.createTimestampStr(true);
        Map<String, String> params = new HashMap<>();
        params.put("appId", YsfPropertiesUtils.getAppId(platformKey));
        params.put("insAcctId", YsfPropertiesUtils.getInsAcctId(platformKey));
        params.put("transTs", transTs);
        params.put("transSeqId", pushNumber);
        if ("0".equals(accountType)) {
            params.put("mobile", account);
            params.put("acctEntityTp", "01");
        } else {
            params.put("openId", account);
            params.put("acctEntityTp", "03");
        }
        // 活动编号
        params.put("pointId", externalProductId);
        // 发放金额
        params.put("pointAt", BigDecimalUtils.toMinute(amount).toString());

        JSONObject busiInfo = new JSONObject();
        busiInfo.put("campaignId", drawId);
        busiInfo.put("campaignName", transDigest);
        params.put("busiInfo", busiInfo.toJSONString());
        params.put("transDigest", transDigest);
        params.put("nonceStr", nonceStr);
        params.put("timestamp", timestamp);
        String str = MapUtil.sortJoin(params, "&", "=", true);
        //通过SHA256进行签名
        String signature;
        try {
            signature = YsfUtils.sign(str, YsfPropertiesUtils.getRsaPrivateKey(platformKey));
        } catch (Exception e) {
            log.error("云闪付红包赠送签名异常：", e);
            return R.fail("云闪付红包赠送失败,签名异常：" + e.getMessage());
        }
        //密钥无需请求
        params.put("signature", signature);
        DESede desede = SecureUtil.desede(HexUtil.decodeHex(YsfPropertiesUtils.getSymmetricKey(platformKey)));
        if (StringUtils.isNotBlank(params.get("mobile"))) {
            params.put("mobile", desede.encryptBase64(params.get("mobile")));
        }
        String result;
        try {
            result = HttpUtil.post(YsfPropertiesUtils.getSendAcquireUrl(platformKey), JSONObject.toJSONString(params));
        } catch (Exception e) {
            log.error("云闪付红包赠送异常：", e);
            return R.fail("云闪付红包赠送失败,接口异常：" + e.getMessage());
        }
        log.info("云闪付红包赠送,订单：{}，请求供应商产品：{}，请求url：{}，请求参数：{}，返回结果：{}", number, externalProductId, YsfPropertiesUtils.getSendAcquireUrl(platformKey), params, result);
        if (StringUtils.isBlank(result)) {
            log.error("云闪付红包赠送失败，接口返回结果为空，{}", result);
            return R.fail("云闪付红包赠送失败,接口返回结果为空");
        }
        JSONObject resultJson = JSONObject.parseObject(result);
        if ("00".equals(resultJson.getString("resp"))) {
            return R.ok(result);
        }
        return R.fail(result);
    }

    /**
     * 赠送积点
     *
     * @param number      订单号
     * @param amount      核销积点数量
     * @param inMode      0-主动领取入账 1-直接入账
     * @param transDigest 积点赠送描述
     * @param account     发放账号
     * @param accountType 发放账号类型 0-手机号，1-openId
     * @return ok 成功，fail 失败
     */
    public static R<Void> memberPointAcquire(Long number, Long amount, String inMode, String transDigest, String account, String accountType, Long platformKey) {
        return memberPointAcquire(number, number.toString(), YsfPropertiesUtils.getMemberPointAcquireSource(platformKey), amount, inMode, transDigest, account, accountType, platformKey);
    }

    /**
     * 赠送积点
     *
     * @param number                   订单号
     * @param pushNumber               取码订单号
     * @param memberPointAcquireSource 赠送积点活动编号（银联提供）
     * @param amount                   核销积点数量
     * @param inMode                   0-主动领取入账 1-直接入账
     * @param transDigest              积点赠送描述
     * @param account                  发放账号
     * @param accountType              发放账号类型 0-手机号，1-openId
     * @return ok 成功，fail 失败
     */
    public static R<Void> memberPointAcquire(Long number, String pushNumber, String memberPointAcquireSource, Long amount, String inMode, String transDigest, String account, String accountType, Long platformKey) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", YsfPropertiesUtils.getAppId(platformKey));
        params.put("sysId", YsfPropertiesUtils.getSysId(platformKey));
        if ("0".equals(accountType)) {
            params.put("mobile", account);
        } else {
            params.put("openId", account);
        }
        params.put("getSource", memberPointAcquireSource);
        params.put("transSeqId", pushNumber);
        params.put("transTs", DateUtils.dateTimeNow());
        params.put("pointAt", amount.toString());
        params.put("transDigest", transDigest);
        params.put("inMode", inMode);
        params.put("timestamp", DateUtils.createTimestampStr(true));
        params.put("nonceStr", IdUtil.simpleUUID().substring(0, 10));
        params.put("descCode", memberPointAcquireSource);
        String str = MapUtil.sortJoin(params, "&", "=", true);
        //通过SHA256进行签名
        String signature;
        try {
            signature = YsfUtils.sign(str, YsfPropertiesUtils.getRsaPrivateKey(platformKey));
        } catch (Exception e) {
            log.error("积点赠送签名异常：", e);
            return R.fail("积点赠送失败,签名异常：" + e.getMessage());
        }
        //密钥无需请求
        params.put("signature", signature);
        DESede desede = SecureUtil.desede(HexUtil.decodeHex(YsfPropertiesUtils.getSymmetricKey(platformKey)));
        params.put("pointAt", desede.encryptBase64(params.get("pointAt")));
        if (StringUtils.isNotBlank(params.get("mobile"))) {
            params.put("mobile", desede.encryptBase64(params.get("mobile")));
        }
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(YsfPropertiesUtils.getMemberPointAcquireUrl(platformKey), JSONObject.toJSONString(params));
        } catch (Exception e) {
            log.error("积点赠送异常：", e);
            return R.fail("积点赠送失败,接口异常：" + e.getMessage());
        }
        log.info("云闪付赠送积点，订单：{}，请求url：{}，请求参数：{}，返回结果：{}", number, YsfPropertiesUtils.getMemberPointAcquireUrl(platformKey), params, result);
        if (StringUtils.isBlank(result)) {
            log.error("云闪付赠送积点失败，接口返回结果为空，{}", result);
            return R.fail("云闪付赠送积点失败,接口返回结果为空");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("00".equals(jsonObject.getString("resp"))) {
            return R.ok(result);
        }
        return R.fail(result);
    }

    /**
     * 核销用户积点
     *
     * @param number      订单号
     * @param amount      核销积点数量
     * @param productName 积点消耗描述 产品名称
     * @param openId      用户手机号
     * @return ok 成功，fail 失败
     */
    public static R<Void> memberPointDeduct(Long number, Long amount, String productName, String openId, Long platformKey) {
        // 积点核销
        Map<String, String> params = new HashMap<>();
        params.put("appId", YsfPropertiesUtils.getAppId(platformKey));
        params.put("sysId", YsfPropertiesUtils.getSysId(platformKey));
        params.put("openId", openId);
        params.put("costSource", YsfPropertiesUtils.getMemberPointDeductSource(platformKey));
        params.put("transSeqId", number.toString());
        params.put("transTs", DateUtils.dateTimeNow());
        params.put("pointAt", amount.toString());
        params.put("transDigest", productName);
        params.put("timestamp", DateUtils.createTimestampStr(true));
        params.put("nonceStr", IdUtil.simpleUUID().substring(0, 10));
        params.put("descCode", YsfPropertiesUtils.getMemberPointDeductSource(platformKey));
        String str = MapUtil.sortJoin(params, "&", "=", true);
        //通过SHA256进行签名
        String signature;
        try {
            signature = YsfUtils.sign(str, YsfPropertiesUtils.getRsaPrivateKey(platformKey));
        } catch (Exception e) {
            log.error("积点扣除签名异常：", e);
            return R.fail("积点扣除失败,签名异常：" + e.getMessage());
        }
        //密钥无需请求
        params.put("signature", signature);
        DESede desede = SecureUtil.desede(HexUtil.decodeHex(YsfPropertiesUtils.getSymmetricKey(platformKey)));
        params.put("pointAt", desede.encryptBase64(params.get("pointAt")));
        if (StringUtils.isNotBlank(params.get("mobile"))) {
            params.put("mobile", desede.encryptBase64(params.get("mobile")));
        }

        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(YsfPropertiesUtils.getMemberPointDeductUrl(platformKey), JSONObject.toJSONString(params));
        } catch (Exception e) {
            log.error("积点扣除异常：", e);
            return R.fail("积点扣除失败,请求异常：" + e.getMessage());
        }
        log.info("云闪付扣除积点，订单：{}，请求url：{}，请求参数：{}，返回结果：{}", number, YsfPropertiesUtils.getMemberPointDeductUrl(platformKey), params, result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("00".equals(jsonObject.getString("resp"))) {
            return R.ok(result);
        }
        return R.fail(jsonObject.getString("msg"));
    }

    /**
     * 查询用户积点余额
     *
     * @param transSeqId 查询流水号
     * @param openId     openId
     * @return 积点余额
     */
    public static Long memberPointBalance(String transSeqId, String openId, Long platformKey) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", YsfPropertiesUtils.getAppId(platformKey));
        params.put("sysId", YsfPropertiesUtils.getSysId(platformKey));
        params.put("openId", openId);
        params.put("transSeqId", transSeqId);
        params.put("transTs", DateUtils.dateTimeNow());
        params.put("backendToken", getBackendToken(YsfPropertiesUtils.getAppId(platformKey), YsfPropertiesUtils.getSecret(platformKey), false, platformKey));
        String str = MapUtil.sortJoin(params, "&", "=", true);
        //通过SHA256进行签名
        String signature;
        try {
            signature = YsfUtils.sign(str, YsfPropertiesUtils.getRsaPrivateKey(platformKey));
        } catch (Exception e) {
            log.error("签名异常：", e);
            throw new ServiceException("积点查询失败[sign error]");
        }
        //密钥无需请求
        params.put("signature", signature);
        DESede desede = SecureUtil.desede(HexUtil.decodeHex(YsfPropertiesUtils.getSymmetricKey(platformKey)));
        if (StringUtils.isNotBlank(params.get("mobile"))) {
            params.put("mobile", desede.encryptBase64(params.get("mobile")));
        }
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(YsfPropertiesUtils.getMemberPointBalanceUrl(platformKey), JSONObject.toJSONString(params));
        } catch (Exception e) {
            log.error("查询积点余额异常：", e);
            throw new ServiceException("查询积点余额失败，请稍后重试");
        }
        log.info("云闪付查询积点余额，查询流水号：{}，请求url：{}，请求参数：{}，返回结果：{}", transSeqId, YsfPropertiesUtils.getMemberPointBalanceUrl(platformKey), params, result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("00".equals(jsonObject.getString("resp"))) {
            JSONObject data = jsonObject.getJSONObject("params");
            try {
                String avlBalance = data.getString("avlBalance");
                return Long.parseLong(YinLianUtil.getDecryptedValue(avlBalance, YsfPropertiesUtils.getSymmetricKey(platformKey)));
            } catch (Exception e) {
                log.error("查询积点余额解密异常：", e);
                throw new ServiceException("查询积点余额失败[解密异常]");
            }
        } else {
            throw new ServiceException("查询积点余额失败[" + jsonObject.getString("msg") + "]");
        }
    }

    /**
     * 发放优惠券
     *
     * @return 200 发放成功，500 发放失败，601 发放异常需请求接口查询或原订单号重发
     */
    public static R<JSONObject> sendCoupon(Long number, String pushNumber, String externalProductId, String account, Long count, String accountType, Long platformKey) {
        if (StringUtils.isBlank(YsfPropertiesUtils.getSendCouponUrl(platformKey))) {
            return R.fail("发券地址未配置");
        }
        try {
            //查询相关参数
            String transTs = DateUtils.getDate("yyyyMMdd");
            String nonceStr = IdUtil.fastSimpleUUID().substring(0, 12);
            String timestamp = DateUtils.createTimestampStr(true);
            Map<String, String> params = new HashMap<>();
            params.put("appId", YsfPropertiesUtils.getAppId(platformKey));
            params.put("transSeqId", pushNumber);
            params.put("transTs", transTs);
            params.put("couponId", externalProductId);
            if ("0".equals(accountType)) {
                params.put("mobile", account);
            } else {
                params.put("openId", account);
            }
            params.put("couponNum", count.toString());
            params.put("nonceStr", nonceStr);
            params.put("timestamp", timestamp);
            params.put("acctEntityTp", YsfPropertiesUtils.getAcctEntityTp(platformKey));
            String str = MapUtil.sortJoin(params, "&", "=", true);
            //通过SHA256进行签名
            String signature;
            try {
                signature = YsfUtils.sign(str, YsfPropertiesUtils.getRsaPrivateKey(platformKey));
            } catch (Exception e) {
                log.error("签名异常：", e);
                return R.fail("签名异常：" + e.getMessage());
            }
            //密钥无需请求
            params.put("signature", signature);
            DESede desede = SecureUtil.desede(HexUtil.decodeHex(YsfPropertiesUtils.getSymmetricKey(platformKey)));
            if (StringUtils.isNotBlank(params.get("mobile"))) {
                params.put("mobile", desede.encryptBase64(params.get("mobile")));
            }
            String result = HttpUtil.post(YsfPropertiesUtils.getSendCouponUrl(platformKey), JSONObject.toJSONString(params));
            log.info("云闪付发放优惠券，发券订单：{}，请求供应商产品：{}，请求url：{}，请求参数：{}，返回结果：{}", number, externalProductId, YsfPropertiesUtils.getSendCouponUrl(platformKey), params, result);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject resultJson = JSONObject.parseObject(result);
                String resp = resultJson.getString("resp");
                if (resp != null && !"00".equals(resp)) {
                    // 失败
                    return R.fail(resp + "_" + resultJson.getString("msg"));
                }
                // 成功
                return R.ok(resultJson.getJSONObject("params"));
            } else {
                return R.warn("发券接口返回信息为空");
            }
        } catch (Exception e) {
            return R.warn(e.getMessage());
        }
    }

    /**
     * 查询优惠券
     *
     * @return 200 发放成功，500 发放失败，601 发放异常需请求接口查询或原订单号重发
     */
    public static R<JSONObject> queryCoupon(Long number, String pushNumber, Date createTime, Long platformKey) {
        if (StringUtils.isBlank(YsfPropertiesUtils.getQueryCouponUrl(platformKey))) {
            return R.warn("发券地址未配置");
        }
        try {
            Map<String, String> params = new HashMap<>();
            params.put("appId", YsfPropertiesUtils.getAppId(platformKey));
            params.put("origTransSeqId", pushNumber);
            //原交易时间
            params.put("origTransTs", DateUtil.format(createTime, DatePattern.PURE_DATE_PATTERN));
            params.put("transSeqId", IdUtil.simpleUUID());
            params.put("transTs", DateUtils.getDate("yyyyMMdd"));
            params.put("backendToken", YsfUtils.getBackendToken(YsfPropertiesUtils.getAppId(platformKey), YsfPropertiesUtils.getSecret(platformKey), false, platformKey));
            String s = HttpUtil.post(YsfPropertiesUtils.getQueryCouponUrl(platformKey), JSONObject.toJSONString(params));
            log.info("云闪付发券结果查询，订单：{}，请求url：{}，请求参数：{}，返回结果：{}", number, YsfPropertiesUtils.getQueryCouponUrl(platformKey), params, s);
            if (StringUtils.isNotEmpty(s)) {
                JSONObject result = JSONObject.parseObject(s);
                String resp = result.getString("resp");
                if (StringUtils.isNotEmpty(resp)) {
                    if ("00".equals(resp)) {
                        // 成功
                        return R.ok(result.getJSONObject("params"));
                    } else {
                        if ("GCnull".equals(resp)) {
                            // 失败 订单不存在 可原订单号重发
                            return R.fail(result.getString("msg"));
                        }
                    }
                    return R.warn("接口返回码不处理，发券结果查询接口返回结果" + s);
                }
            }
            return R.warn("发券结果查询接口返回结果为空");
        } catch (Exception e) {
            return R.warn(e.getMessage());
        }
    }

    /**
     * 赠送用户62会员
     *
     * @param mobile     手机号
     * @param number     订单号
     * @param memberType 开通会员类型 01-月卡 02-季卡 03-年卡 05-食神卡 08-svip
     */
    public static Boolean sendMemberVipBalance(String mobile, String number, String memberType, Long platformKey) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", YsfPropertiesUtils.getAppId(platformKey));
        params.put("sysId", YsfPropertiesUtils.getSysId(platformKey));
        params.put("mobile", mobile);
        /*
         * 开通会员类型 01-月卡 02-季卡 03-年卡 05-食神卡 08-svip
         */
        params.put("memberType", memberType);
        params.put("transSeqId", number);
        params.put("transTs", DateUtils.dateTimeNow());
        params.put("isLimit", "1");
        params.put("timestamp", DateUtils.createTimestampStr(true));
        params.put("nonceStr", IdUtil.simpleUUID().substring(0, 12));
        String str = MapUtil.sortJoin(params, "&", "=", true);
        //通过SHA256进行签名
        String signature;
        try {
            signature = YsfUtils.sign(str, YsfPropertiesUtils.getRsaPrivateKey(platformKey));
        } catch (Exception e) {
            log.error("发放62会员加签异常", e);
            return false;
        }
        //密钥无需请求
        params.put("signature", signature);
        DESede desede = SecureUtil.desede(HexUtil.decodeHex(YsfPropertiesUtils.getSymmetricKey(platformKey)));
        mobile = desede.encryptBase64(mobile);
        params.put("mobile", mobile);
        String body = JsonUtils.toJsonString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.createPost(YsfPropertiesUtils.getMemberVipAcquireUrl(platformKey)).body(body).execute().body();
        } catch (Exception e) {
            log.error("发放用户62会员异常：", e);
            return null;
        }
        log.info("发放用户62会员,请求参数：{},返回结果：{}", body, result);
        Dict dict = JsonUtils.parseMap(result);
        if (null == dict) {
            return null;
        }
        String resp = dict.getStr("resp");
        return "00".equals(resp);
    }

    /**
     * 查询用户62会员状态
     *
     * @param mobile 手机号
     */
    public static MemberVipBalanceVo queryMemberVipBalance(String mobile, String appId, String secret, String symmetricKey, Long platformKey) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("sysId", YsfPropertiesUtils.getSysId(platformKey));
        params.put("mobile", mobile);
        params.put("backendToken", getBackendToken(appId, secret, false, platformKey));
        DESede desede = SecureUtil.desede(HexUtil.decodeHex(symmetricKey));
        mobile = desede.encryptBase64(mobile);
        params.put("mobile", mobile);

        String body = JsonUtils.toJsonString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.createPost(YsfPropertiesUtils.getMemberVipBalanceUrl(platformKey)).body(body).execute().body();
        } catch (Exception e) {
            log.error("查询用户62会员状态异常：", e);
            return null;
        }
        log.info("查询用户62会员状态,请求参数：{},返回结果：{}", body, result);
        Dict dict = JsonUtils.parseMap(result);
        if (null == dict) {
            return null;
        }
        // {"msg":"成功","resp":"00","params":{"newMember":"02","yearValid":"1","seasonValid":"0","beginTime":"20210824085721","endTime":"20230926085721","memberType":"03","monthValid":"0","status":"01"}}
        String resp = dict.getStr("resp");
        String msg = dict.getStr("msg");
        if ("00".equals(resp)) {
            return BeanUtil.toBean(dict.getBean("params"), MemberVipBalanceVo.class);
        } else {
            log.error("查询用户62会员状态失败：{}", result);
        }
        return null;
    }

    /**
     * 获取接口访问令牌
     *
     * @param appid           AppID
     * @param secret          密钥
     * @param newBackendToken 是否获取新的,true直接获取新的，false从redis中取
     * @return 访问令牌
     */
    public static String getBackendToken(String appid, String secret, boolean newBackendToken, Long platformKey) {
        String key = ZlyyhConstants.BACKEND_TOKEN_REDIS_KEY + appid;
        if (!newBackendToken) {
            // 从redis中获取，如果存在，直接返回，如果不存在，再做处理
            String s = RedisUtils.getCacheObject(key);
            if (StringUtils.isNotEmpty(s)) {
                return s;
            }
        }
        LockTemplate lockTemplate = SpringUtils.getBean(LockTemplate.class);
        final LockInfo lockInfo = lockTemplate.lock(appid, 20000L, 3000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            log.error("获取云闪付基础访问令牌失败，已有锁在执行");
            return null;
        }
        // 获取锁成功，处理业务
        try {
            // 生成随机串
            String nonceStr = RandomUtil.randomString(12);
            // 生成时间戳，秒
            String timestamp = DateUtils.createTimestampStr(true);

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appId", appid);
            paramMap.put("nonceStr", nonceStr);
            paramMap.put("secret", secret);
            paramMap.put("timestamp", timestamp);
            // 以键值对的形式拼接成字符串
            String str = MapUtil.sortJoin(paramMap, "&", "=", false);
            // 通过SHA256进行签名
            String signature = SecureUtil.sha256(str);
            // 密钥无需请求
            paramMap.remove("secret");
            paramMap.put("signature", signature);
            // 发送https 请求
            String result;
            try {
                result = HttpRequest.post(YsfPropertiesUtils.getBackendTokenUrl(platformKey))
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("Accept", "application/json")
                    .body(JsonUtils.toJsonString(paramMap), "application/json")
                    .execute().body();
            } catch (Exception e) {
                log.error("云闪付请求获取backendToken异常，异常信息:", e);
                return null;
            }
            log.info("云闪付获取backendToken请求参数：{}，返回结果：{}", JsonUtils.toJsonString(paramMap), result);
            BackendTokenEntity jsonObject = JsonUtils.parseObject(result, BackendTokenEntity.class);
            if (null == jsonObject) {
                return null;
            }
            String resp = jsonObject.getResp();
            String msg = jsonObject.getMsg();
            if ("00".equals(resp)) {
                Map<String, String> resultObject = jsonObject.getParams();
                String backendToken = resultObject.get("backendToken");
                String expires = resultObject.get("expiresIn");
                int expiresIn;
                // 没时间，默认一小时
                if (StringUtils.isEmpty(expires)) {
                    // 单位秒
                    expiresIn = 60 * 60;
                } else {
                    // 有时间，去七成
                    expiresIn = Integer.parseInt(expires);
                    expiresIn = new Double(expiresIn * 0.7).intValue();
                }
                RedisUtils.setCacheObject(key, backendToken, Duration.ofSeconds(expiresIn));
                return backendToken;
            } else {
                log.info("云闪付获取backendToken失败，msg={}", msg);
                return null;
            }
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
        //结束
    }

    /**
     * 获取接口访问令牌
     *
     * @param appid  AppID
     * @param secret 密钥
     * @return 访问令牌
     */
    public static String getBackendTokenTest(String appid, String secret, String url) {
        // 生成随机串
        String nonceStr = RandomUtil.randomString(12);
        // 生成时间戳，秒
        String timestamp = DateUtils.createTimestampStr(true);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appId", appid);
        paramMap.put("nonceStr", nonceStr);
        paramMap.put("secret", secret);
        paramMap.put("timestamp", timestamp);
        // 以键值对的形式拼接成字符串
        String str = MapUtil.sortJoin(paramMap, "&", "=", false);
        // 通过SHA256进行签名
        String signature = SecureUtil.sha256(str);
        // 密钥无需请求
        paramMap.remove("secret");
        paramMap.put("signature", signature);
        // 发送https 请求
        String result;
        try {
            result = HttpRequest.post(url)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Accept", "application/json")
                .body(JsonUtils.toJsonString(paramMap), "application/json")
                .execute().body();
        } catch (Exception e) {
            log.error("云闪付请求获取backendToken异常，异常信息:", e);
            return null;
        }
        log.info("云闪付获取backendToken请求参数：{}，返回结果：{}", JsonUtils.toJsonString(paramMap), result);
        BackendTokenEntity jsonObject = JsonUtils.parseObject(result, BackendTokenEntity.class);
        if (null == jsonObject) {
            return null;
        }
        String resp = jsonObject.getResp();
        String msg = jsonObject.getMsg();
        if ("00".equals(resp)) {
            Map<String, String> resultObject = jsonObject.getParams();
            return resultObject.get("backendToken");
        } else {
            log.info("云闪付获取backendToken失败，msg={}", msg);
            return null;
        }
    }

    /**
     * RSA签名
     *
     * @param value      签名字符串
     * @param privateKey RSA私钥
     * @return 结果
     * @throws Exception 异常
     */
    public static String sign(String value, String privateKey) throws Exception {
        byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyf.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(priKey);
        signature.update(value.getBytes(StandardCharsets.UTF_8));
        byte[] signed = signature.sign();
        return Base64.encode(signed);
    }

    /**
     * 验签
     *
     * @param sign      原签名
     * @param value     需要验签的内容
     * @param publicKey 公钥
     * @return true 验签通过，false验签不通过
     */
    public static Boolean vitSign(String sign, String value, String publicKey) throws Exception {
        byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        PublicKey priKey = keyf.generatePublic(keySpec);
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initVerify(priKey);
        signature.update(value.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64Utils.decodeFromString(sign));
    }

    public static void main(String[] args) {
//        String appId = "d27c0217490d4e35a901abb2e874f383";
//        String secret = "92d7f2f9e56243618077bebf34bb8da0";
//        String openId = "qt0ox7zbYK0kV2D2iH1jk+iG2kuggLb3APUmZApbCtijevf3qtr9sJNDpKPj5cpA";
//        String activityId = "HD2023101800301";
//        List<String> missionIdList = new ArrayList<>();
//        missionIdList.add("JYRW2023101800509");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("appId", appId);
////        param.put("backendToken", getBackendTokenTest(appId, secret, "https://open.95516.com/open/access/1.0/backendToken"));
//        param.put("backendToken", "08abf63e200000491RfzZGk1");
//        param.put("openId", openId);
//        param.put("activityId", activityId);
//        param.put("missionIdList", missionIdList);
//        param.put("logId", IdUtil.getSnowflakeNextIdStr());
//        String result = HttpUtil.post("https://open.95516.com/open/access/1.0/searchProgress", JSONObject.toJSONString(param));
//        log.info("用户任务进度查询返回结果：{}", result);
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        JSONObject params = jsonObject.getJSONObject("params");
//        String missionProcessMap = params.getString("missionProcessMap");
//        DESede desede = SecureUtil.desede(HexUtil.decodeHex("9e3d1fdf342370b926d564e5d91cba0b9e3d1fdf342370b9"));
//        missionProcessMap = desede.decryptStr(missionProcessMap);
//        log.info("解密后：{}", missionProcessMap);

        String str = "gL416Eiprtewi34YEJ6633j8jptV6/D1RtcswyzqKnNWEC6Z81gen2jIt2iukAWBZ0aWOKPMD7OJ1uyXG3/ZwqbqtA0HIIZxJ685tawcCx3FXZ5IW4MlhLUAhPCLawzLaTaiLtfp5dGpFn67e6+7DmFTIfDvnedfcf+Lsk044AYt0pd/YdoVA/JuRYm2U5ADjf0ypb935s4v0feSq2xUJXmRhcA0Fg2r/EdRz5S2UcY1+jIfV0CQnhivarwh5mShgAfMfVG6+anDkTw7Nmx3Pb8Tm8UpjguiH245XrQ/NgvO4UZRR/ZWe4pov+wwLk1nG7F/CiUw831TSwiQBVID9J2CPBfbR207TxIWJ3DnTUJiJDWCX2Phu/64GhAA6ypBUyZh5u+8B3wSInrHs2HY8e7CWEDnjJOxSJLbpR5n881fqXF5yQOj6n9NQg0duSXXflSk71Icoz8=";
        String key = "9e3d1fdf342370b926d564e5d91cba0b9e3d1fdf342370b9";
        DESede desede = SecureUtil.desede(HexUtil.decodeHex(key));
        log.info("解密后：{}", desede.decryptStr(str));
//        String appId = "e60611309c5d4e77b15baf6b4e48292c";
//        String secret = "151e06ec873747fcbd024c54d263b27f";
//        String openId = "bsrhPJ+6nhRkdQI+OmzWoMDcy8Pjkm21YW/1p3dHJoLF9WyPWB/LrsqAW6k0zQ9R";
//        String activityId = "HD2023082800258";
//        List<String> missionIdList = new ArrayList<>();
//        missionIdList.add("JYRW2023082800433");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("appId", appId);
//        param.put("backendToken", getBackendTokenTest(appId, secret, "https://open.95516.com/open/access/1.0/backendToken"));
//        param.put("openId", openId);
//        param.put("activityId", activityId);
//        param.put("missionIdList", missionIdList);
//        param.put("logId", IdUtil.getSnowflakeNextIdStr());
//        String result = HttpUtil.post("https://open.95516.com/open/access/1.0/searchProgress", JSONObject.toJSONString(param));
//        log.info("用户任务进度查询返回结果：{}", result);
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        JSONObject params = jsonObject.getJSONObject("params");
//        String missionProcessMap = params.getString("missionProcessMap");
//        DESede desede = SecureUtil.desede(HexUtil.decodeHex("d015eafe2fc74fa4a1c12f02454cf4c1d015eafe2fc74fa4"));
//        missionProcessMap = desede.decryptStr(missionProcessMap);
//        log.info("解密后：{}", missionProcessMap);
//        Map<String, Object> contentData = new HashMap<>();
//        contentData.put("appId", "868f070bed8942f3bef25f8fae5bd285");
//        contentData.put("openId", "7Y9bOE9PmeifDRDi9FljAOFAznfEs0hMmBWlfQ6SRB7IAsGUu30hAZzgCBNxGni0");
//        contentData.put("backendToken", "07a7f98c2002002a1sbIrqZ8");
//        contentData.put("mpId", "c764ebf2e06764e8");
//        contentData.put("templateId", "6625badf068e4e6aa48752442465de6e");
//        contentData.put("desc", "这个是消息通知测试");
//        contentData.put("uiType", "1");
//        contentData.put("destType", "applet");
//        contentData.put("destInfo", contentData.get("mpId"));
//        List<Map<String ,String>> list = new ArrayList<Map<String, String>>();
//        Map<String,String> map = new HashMap<>();
//        map.put("key","MU00000204");
//        map.put("value","这个是通知详情");
//        list.add(map);
//        contentData.put("valueList", list);
//
//        String s = HttpUtil.post("https://open.95516.com/open/access/1.0/applet.msg", JSONObject.toJSONString(contentData));
//        log.info("通知模板ID：{},通知结果：{},通知内容：{}", "6625badf068e4e6aa48752442465de6e", s, JSONObject.toJSONString(contentData));

//        System.out.println(YsfUtils.getBackendTokenTest("2ba457e4a0e944b0b495347dbb22f27e", "7209ef4d04694d9a9ff48db7b21651ab", "https://open.95516.com/open/access/1.0/backendToken"));

//        String appId = "fa83ef42f3874697a05f5670c62999c5";
//        String secret = "664a7c84048f4559a0a159b0733dee61";
//        String queryCouponUrl = "https://open.95516.com/open/access/1.0/point.acquire";
//
//        String transTs = DateUtils.getDate("yyyyMMdd");
//        String nonceStr = IdUtil.fastSimpleUUID().substring(0, 12);
//        String timestamp = DateUtils.createTimestampStr(true);
//        Map<String, String> params = new HashMap<>();
//        params.put("appId", appId);
//        params.put("insAcctId", "P230327162125724");
//        params.put("transTs", transTs);
//        params.put("transSeqId", IdUtil.getSnowflakeNextIdStr());
//        params.put("mobile", "13095658720");
////        params.put("acctEntityTp", YSF_PROPERTIES.getAcctEntityTp());
//        params.put("acctEntityTp", "01");
//        // 活动编号
//        params.put("pointId", "4120060413537864");
//        // 发放金额
//        params.put("pointAt", "1");
//
//        JSONObject busiInfo = new JSONObject();
//        busiInfo.put("campaignId", "12355");
//        busiInfo.put("campaignName", "测试");
//        params.put("busiInfo", busiInfo.toJSONString());
//        params.put("transDigest", "测试");
//        params.put("nonceStr", nonceStr);
//        params.put("timestamp", timestamp);
//        String str = MapUtil.sortJoin(params, "&", "=", true);
//        //通过SHA256进行签名
//        String signature;
//        try {
//            signature = YsfUtils.sign(str, "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAKeJfobd84HmjBkYFGjvAVdYeLg1zS+XOTuCuPqGuuGTsYaorog2zRnBWSUQ0yuMteJnSIC3vxivR46Cd2IyV9bD7H1/SuRiFtMgdABAyNykLMGRV61V3lzPqnDxBkqgK0R5QYsvs0OS0gbdZUNPSxuFOfnptdq2GCxJOD9DCxMZAgMBAAECgYEApo7YzTfnGKOdcG0yDUhfavi3u1sxjDipW3KQd/Bt5kkw2pDkQuNIcGx6NZFOfyM6x8Sqnd0PDHlliFZIXcVy8Jszf4eCLIEbykptDtujjrBV01PqtKYVB3WgJP9o/baLftZsRCFTPb6psqczwli5kD83pCHNkklA7l5rZG/WcQECQQD6rNZFuckAta6rTkuFSeTlxPT3IBu90gsE9xxkiTI5uD+0B8+q7mo8bZrRY0hRV7slpkGqQbXCu/2f+q7Ie6iJAkEAqxiOJyXrbIl/RnKsTDZ/RsC9qCxcTb3DBwO55CfsLu74WMTKd3unC3EjtSn7IazGDIPjRjaG6rgSJ1n8B9FSEQJBAPAWdA9KFqMQX+AA2EIr+Qi8cGb0oL1YnGdACjicUreHqbPTO2oaeTOxQnPDpHMMFNnFd+UKlHyTsyHzZk3sagkCQQCVRqBIAaqMkM4tzcEL4YRcW69dOg7yePzect7N9BL5w9+Du3aWlpjgv76SwmTsNYy5wJwbV1mREjYshTMCMxuxAkEAiHYaeYsitAgFhiLWO63uED3HjL8OPNrL8myJwbbAD0N1tR+EqQt9HYhSbjzFbqmW7EqnEFM44yOvEoIqUYUUfg==");
//        } catch (Exception e) {
//            log.error("签名异常：", e);
//            return;
//        }
//        //密钥无需请求
//        params.put("signature", signature);
//        DESede desede = SecureUtil.desede(HexUtil.decodeHex("e3df37c73bc7e9f4583d3ecb8a7ad6c2e3df37c73bc7e9f4"));
//        params.put("mobile", desede.encryptBase64(params.get("mobile")));
//
//        String result;
//        try {
//            result = HttpUtil.post(queryCouponUrl, JSONObject.toJSONString(params));
//        } catch (Exception e) {
//            log.error("云闪付红包赠送异常：", e);
//            return;
//        }
//        log.info("请求参数：{}，返回结果：{}", params, result);

//        Map<String, String> params = new HashMap<>();
//        params.put("appId", appId);
//        params.put("transSeqId", IdUtil.getSnowflakeNextIdStr());
//        params.put("transTs", DateUtils.getDate("yyyyMMdd"));
//        params.put("nonceStr", RandomUtil.randomString(12));
//        params.put("mobile", "17767132971");
//        params.put("activityNo", "3220230628041656");
//        params.put("acctEntityTp", "03");
//        params.put("timestamp", DateUtils.createTimestampStr(true));
//        String str = MapUtil.sortJoin(params, "&", "=", true);
//        //通过SHA256进行签名
//        String signature;
//        try {
//            signature = YsfUtils.sign(str, "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAKeJfobd84HmjBkYFGjvAVdYeLg1zS+XOTuCuPqGuuGTsYaorog2zRnBWSUQ0yuMteJnSIC3vxivR46Cd2IyV9bD7H1/SuRiFtMgdABAyNykLMGRV61V3lzPqnDxBkqgK0R5QYsvs0OS0gbdZUNPSxuFOfnptdq2GCxJOD9DCxMZAgMBAAECgYEApo7YzTfnGKOdcG0yDUhfavi3u1sxjDipW3KQd/Bt5kkw2pDkQuNIcGx6NZFOfyM6x8Sqnd0PDHlliFZIXcVy8Jszf4eCLIEbykptDtujjrBV01PqtKYVB3WgJP9o/baLftZsRCFTPb6psqczwli5kD83pCHNkklA7l5rZG/WcQECQQD6rNZFuckAta6rTkuFSeTlxPT3IBu90gsE9xxkiTI5uD+0B8+q7mo8bZrRY0hRV7slpkGqQbXCu/2f+q7Ie6iJAkEAqxiOJyXrbIl/RnKsTDZ/RsC9qCxcTb3DBwO55CfsLu74WMTKd3unC3EjtSn7IazGDIPjRjaG6rgSJ1n8B9FSEQJBAPAWdA9KFqMQX+AA2EIr+Qi8cGb0oL1YnGdACjicUreHqbPTO2oaeTOxQnPDpHMMFNnFd+UKlHyTsyHzZk3sagkCQQCVRqBIAaqMkM4tzcEL4YRcW69dOg7yePzect7N9BL5w9+Du3aWlpjgv76SwmTsNYy5wJwbV1mREjYshTMCMxuxAkEAiHYaeYsitAgFhiLWO63uED3HjL8OPNrL8myJwbbAD0N1tR+EqQt9HYhSbjzFbqmW7EqnEFM44yOvEoIqUYUUfg==");
//        } catch (Exception e) {
//            log.error("签名异常：", e);
//            return;
//        }
//        //密钥无需请求
//        params.put("signature", signature);
//        DESede desede = SecureUtil.desede(HexUtil.decodeHex("e3df37c73bc7e9f4583d3ecb8a7ad6c2e3df37c73bc7e9f4"));
//        params.put("mobile", desede.encryptBase64(params.get("mobile")));
//        String s = HttpUtil.post(queryCouponUrl, JSONObject.toJSONString(params));
//        log.info("请求参数：{}，返回结果：{}", params, s);

//        Map<String, String> params = new HashMap<>();
//        params.put("appId", appId);
//        params.put("origTransSeqId", "1673874154526142464");
//        //原交易时间
//        params.put("origTransTs", "202306028");
//        params.put("transSeqId", IdUtil.simpleUUID());
//        params.put("transTs", DateUtils.getDate("yyyyMMdd"));
//        params.put("backendToken", YsfUtils.getBackendTokenTest(appId, secret, "https://open.95516.com/open/access/1.0/backendToken"));
//        String s = HttpUtil.post(queryCouponUrl, JSONObject.toJSONString(params));
//        log.info("云闪付发券结果查询，请求参数：{}，返回结果：{}", params, s);

//        Map<String, String> params = new HashMap<>();
//        params.put("appId", "fa83ef42f3874697a05f5670c62999c5");
//        params.put("sysId", "2007");
//        params.put("mobile", "13675843378");
//        params.put("getSource", "660302642135965696");
//        params.put("transSeqId", "T"+"1661718590843572224");
//        params.put("transTs", DateUtils.dateTimeNow());
//        params.put("pointAt", "400");
//        params.put("transDigest", "660302642135965696");
//        params.put("inMode", "01");
//        params.put("timestamp", DateUtils.createTimestampStr(true));
//        params.put("nonceStr", IdUtil.simpleUUID().substring(0, 10));
//        params.put("descCode", "660302642135965696");
//        String str = MapUtil.sortJoin(params, "&", "=", true);
//        //通过SHA256进行签名
//        String signature;
//        try {
//            signature = YsfUtils.sign(str, "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAKeJfobd84HmjBkYFGjvAVdYeLg1zS+XOTuCuPqGuuGTsYaorog2zRnBWSUQ0yuMteJnSIC3vxivR46Cd2IyV9bD7H1/SuRiFtMgdABAyNykLMGRV61V3lzPqnDxBkqgK0R5QYsvs0OS0gbdZUNPSxuFOfnptdq2GCxJOD9DCxMZAgMBAAECgYEApo7YzTfnGKOdcG0yDUhfavi3u1sxjDipW3KQd/Bt5kkw2pDkQuNIcGx6NZFOfyM6x8Sqnd0PDHlliFZIXcVy8Jszf4eCLIEbykptDtujjrBV01PqtKYVB3WgJP9o/baLftZsRCFTPb6psqczwli5kD83pCHNkklA7l5rZG/WcQECQQD6rNZFuckAta6rTkuFSeTlxPT3IBu90gsE9xxkiTI5uD+0B8+q7mo8bZrRY0hRV7slpkGqQbXCu/2f+q7Ie6iJAkEAqxiOJyXrbIl/RnKsTDZ/RsC9qCxcTb3DBwO55CfsLu74WMTKd3unC3EjtSn7IazGDIPjRjaG6rgSJ1n8B9FSEQJBAPAWdA9KFqMQX+AA2EIr+Qi8cGb0oL1YnGdACjicUreHqbPTO2oaeTOxQnPDpHMMFNnFd+UKlHyTsyHzZk3sagkCQQCVRqBIAaqMkM4tzcEL4YRcW69dOg7yePzect7N9BL5w9+Du3aWlpjgv76SwmTsNYy5wJwbV1mREjYshTMCMxuxAkEAiHYaeYsitAgFhiLWO63uED3HjL8OPNrL8myJwbbAD0N1tR+EqQt9HYhSbjzFbqmW7EqnEFM44yOvEoIqUYUUfg==");
//        } catch (Exception e) {
//            log.error("积点赠送签名异常：", e);
//            return;
//        }
//        //密钥无需请求
//        params.put("signature", signature);
//        DESede desede = SecureUtil.desede(HexUtil.decodeHex("e3df37c73bc7e9f4583d3ecb8a7ad6c2e3df37c73bc7e9f4"));
//        params.put("mobile", desede.encryptBase64(params.get("mobile")));
//        params.put("pointAt", desede.encryptBase64(params.get("pointAt")));
//
//        // 发送https 请求
//        String result;
//        try {
//            result = HttpUtil.post("https://open.95516.com/open/access/1.0/memberPointAcquire", JSONObject.toJSONString(params));
//        } catch (Exception e) {
//            log.error("积点赠送异常：", e);
//            return;
//        }
//        log.info("云闪付赠送积点，返回结果：{}",result);
    }
}
