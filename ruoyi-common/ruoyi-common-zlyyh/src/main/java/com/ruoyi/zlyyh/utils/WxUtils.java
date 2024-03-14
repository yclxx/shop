package com.ruoyi.zlyyh.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.vo.OcrBizLicenseVo;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信帮助类
 *
 * @author 25487
 * @date 20231017
 */
@Slf4j
public class WxUtils {

    /**
     * 发送模板消息
     *
     * @param accessToken token
     * @param openId      用户openId
     * @param templateId  模板Id
     * @param page        页面
     * @param msgData     消息
     */
    public static void sendTemplateMessage(String accessToken, String openId, String templateId, String page, Map<String, Object> msgData) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
        Map<String, Object> params = new HashMap<>();
        params.put("miniprogram_state", "formal");
        params.put("lang", "zh_CN");
        params.put("template_id", templateId);
        params.put("touser", openId);
        params.put("page", page);
        params.put("data", msgData);
        String post = HttpUtil.post(url, JsonUtils.toJsonString(params));
        log.info("微信消息推送，请求信息：{}，返回结果：{}", params, post);
    }

    public static byte[] genQrCode(String accessToken, String scene, String page, String env_version) {
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
        Map<String, Object> body = new HashMap<>();
        if (StringUtils.isNotBlank(scene)) {
            body.put("scene", scene);
        } else {
            body.put("scene", "1");
        }
        if (StringUtils.isNotBlank(page)) {
            body.put("page", page);
        }
        if (StringUtils.isNotBlank(env_version)) {
            body.put("env_version", env_version);
            if (!"release".equals(env_version)) {
                body.put("check_path", false);
            }
        }
        // 透明图
//        body.put("is_hyaline", true);
        cn.hutool.http.HttpResponse execute = HttpUtil.createPost(url).body(JsonUtils.toJsonString(body)).execute();
        if (execute.header("Content-Type").contains("application/json;")) {
            log.error("生成小程序码失败，请求参数：{}，返回结果：{}", body, execute.body());
            throw new ServiceException("生成小程序码失败，请稍后重试");
        }
        return execute.bodyBytes();
    }

    /**
     * 获取微信接口基础访问令牌
     *
     * @param appId  AppID
     * @param secret 密钥
     * @return 基础访问令牌
     */
    public static String getAccessToken(String appId, String secret, String url,Boolean flag) {
        // 获取基础访问令牌
        String accessTokenRedisKey = "accessToken:" + appId;
        String accessToken = RedisUtils.getCacheObject(accessTokenRedisKey);
        if (StringUtils.isNotEmpty(accessToken) && !flag) {
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

    /**
     * 获取微信接口基础访问令牌
     *
     * @param appId  AppID
     * @param secret 密钥
     * @return 基础访问令牌
     */
    public static String getAccessToken(String appId, String secret,Boolean flag) {
        return getAccessToken(appId, secret, "https://api.weixin.qq.com/cgi-bin/token",flag);
    }

    /**
     * 微信小程序支付
     *
     * @param number                订单号
     * @param url                   请求地址
     * @param appid                 AppId
     * @param mchid                 商户号
     * @param description           商品信息
     * @param amountTotal           支付金额，单位分
     * @param openId                用户openId
     * @param notify_url            回调地址
     * @param platform_merchant_key 商户证书私钥
     * @param merchantSerialNumber  商户证书序列号
     * @param apiV3Key              微信支付APIv3秘钥
     * @return 调起小程序支付所需参数
     * @throws IOException              IOException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeyException      InvalidKeyException
     * @throws SignatureException       InvalidKeyException
     */
    public static Map<String, String> wxPay(String number, String url, String appid, String mchid, String description, Integer amountTotal, String openId, String notify_url, String platform_merchant_key, String merchantSerialNumber, String apiV3Key) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
            getCertInput(platform_merchant_key));
        // 自动更新证书功能
        HttpClient httpClient = getHttpClient(mchid, merchantSerialNumber, merchantPrivateKey, apiV3Key);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("mchid", mchid)
            .put("appid", appid)
            .put("description", description)
            .put("notify_url", notify_url)
            .put("out_trade_no", number);
        rootNode.putObject("amount")
            .put("total", amountTotal);
        rootNode.putObject("payer")
            .put("openid", openId);

        objectMapper.writeValue(bos, rootNode);

        HttpPost httpPost = getHttpPost(url);
        httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
        HttpResponse response = httpClient.execute(httpPost);

        String bodyAsString = EntityUtils.toString(response.getEntity());

        Dict dict = JsonUtils.parseMap(bodyAsString);
        if (null == dict || StringUtils.isEmpty(dict.getStr("prepay_id"))) {
            log.info("微信支付异常，请求参数：{}，返回信息：{}", httpPost.getEntity(), bodyAsString);
            return null;
        }
        String prepay_id = "prepay_id=" + dict.get("prepay_id");
        String nonceStr = IdUtil.simpleUUID();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String pendingPaySign = appid + "\n" + timeStamp + "\n" + nonceStr + "\n" + prepay_id + "\n";
        String paySign = sign256(pendingPaySign, merchantPrivateKey);
        Map<String, String> map = new HashMap<>();
        map.put("appId", appid);
        map.put("timeStamp", timeStamp);
        map.put("nonceStr", nonceStr);
        map.put("package", prepay_id);
        map.put("signType", "RSA");
        map.put("paySign", paySign);
        return map;
    }

    /**
     * 微信小程序订单查询
     *
     * @param url                   请求地址
     * @param number                订单号
     * @param mchid                 商户号
     * @param platform_merchant_key 商户证书私钥
     * @param merchantSerialNumber  商户证书序列号
     * @param apiV3Key              微信支付APIv3秘钥
     * @return 查询结果
     * @throws IOException IOException
     */
    public static String queryWxOrder(String url, String number, String mchid, String platform_merchant_key, String merchantSerialNumber, String apiV3Key) throws IOException {
        String queryOrderUrl = url + "/" + number + "?mchid=" + mchid;
        return queryWxOrder(queryOrderUrl, mchid, platform_merchant_key, merchantSerialNumber, apiV3Key);
    }

    /**
     * 微信小程序退款查询
     *
     * @param url                   请求地址
     * @param number                退款单号
     * @param mchid                 商户号
     * @param platform_merchant_key 商户证书私钥
     * @param merchantSerialNumber  商户证书序列号
     * @param apiV3Key              微信支付APIv3秘钥
     * @return 查询结果
     * @throws IOException IOException
     */
    public static String queryWxRefundOrder(String url, String number, String mchid, String platform_merchant_key, String merchantSerialNumber, String apiV3Key) throws IOException {
        String queryOrderUrl = url + "/" + number;
        return queryWxOrder(queryOrderUrl, mchid, platform_merchant_key, merchantSerialNumber, apiV3Key);
    }

    /**
     * 微信小程序订单查询包含退款订单，自行根据地址区分
     *
     * @param url                   请求地址
     * @param mchid                 商户号
     * @param platform_merchant_key 商户证书私钥
     * @param merchantSerialNumber  商户证书序列号
     * @param apiV3Key              微信支付APIv3秘钥
     * @return 查询结果
     * @throws IOException IOException
     */
    private static String queryWxOrder(String url, String mchid, String platform_merchant_key, String merchantSerialNumber, String apiV3Key) throws IOException {
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
            getCertInput(platform_merchant_key));
        // 自动更新证书功能
        HttpClient httpClient = getHttpClient(mchid, merchantSerialNumber, merchantPrivateKey, apiV3Key);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        HttpResponse response = httpClient.execute(httpGet);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 微信小程序申请退款
     *
     * @param number                原订单号，我方的
     * @param out_refund_no         生成的退款订单号，我方的
     * @param url                   请求地址
     * @param mchid                 商户号
     * @param reason                退款原因，非必填
     * @param refundAmount          退款金额，单位分
     * @param amountTotal           原订单金额，单位分
     * @param notify_url            退款回调地址
     * @param platform_merchant_key 商户证书私钥
     * @param merchantSerialNumber  商户证书序列号
     * @param apiV3Key              微信支付APIv3秘钥
     * @return 请求结果
     * @throws IOException IOException
     */
    public static String wxRefund(String number, String out_refund_no, String url, String mchid, String reason, Integer refundAmount, Integer amountTotal, String notify_url, String platform_merchant_key, String merchantSerialNumber, String apiV3Key) throws IOException {
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
            getCertInput(platform_merchant_key));
        // 自动更新证书功能
        HttpClient httpClient = getHttpClient(mchid, merchantSerialNumber, merchantPrivateKey, apiV3Key);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("out_trade_no", number)
            .put("out_refund_no", out_refund_no)
            .put("notify_url", notify_url);
        if (StringUtils.isNotEmpty(reason)) {
            rootNode.put("reason", reason);
        }
        rootNode.putObject("amount")
            .put("refund", refundAmount)
            .put("total", amountTotal)
            .put("currency", "CNY");

        objectMapper.writeValue(bos, rootNode);

        HttpPost httpPost = getHttpPost(url);
        httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
        HttpResponse response = httpClient.execute(httpPost);
        String result = EntityUtils.toString(response.getEntity());
        log.info("微信退款请求参数：{},返回结果{}", rootNode, result);
        return result;
    }

    private final static int KEY_SIZE = 128;

    /**
     * 解密
     *
     * @param aesKey         密钥
     * @param associatedData 加密数据
     * @param nonce          随机字符串
     * @param ciphertext     数据密文
     * @return 解密后内容
     * @throws GeneralSecurityException GeneralSecurityException
     */
    public static String decryptToString(byte[] aesKey, byte[] associatedData, byte[] nonce, String ciphertext)
        throws GeneralSecurityException {
        if (aesKey.length != 32) {
            throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(KEY_SIZE, nonce);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);

            return new String(cipher.doFinal(java.util.Base64.getDecoder().decode(ciphertext)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 自动更新证书功能
     *
     * @param mchid                商户号
     * @param merchantSerialNumber 证书序列号
     * @param merchantPrivateKey   证书私钥
     * @param apiV3Key             微信支付APIv3秘钥
     * @return HttpClient
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    private static HttpClient getHttpClient(String mchid, String merchantSerialNumber, PrivateKey merchantPrivateKey, String apiV3Key) throws UnsupportedEncodingException {
        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
            new WechatPay2Credentials(mchid, new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)),
            apiV3Key.getBytes(StandardCharsets.UTF_8));

        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
            .withMerchant(mchid, merchantSerialNumber, merchantPrivateKey)
            .withValidator(new WechatPay2Validator(verifier));
        // ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient
        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
        return builder.build();
    }

    /**
     * SHA256WithRSA签名
     *
     * @param data       签名数据
     * @param privateKey 私钥
     * @return 结果
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeyException      InvalidKeyException
     * @throws SignatureException       SignatureException
     */
    private static String sign256(String data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException,
        SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encode(signature.sign());
    }

    private static HttpPost getHttpPost(String url) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");

        return httpPost;
    }

    public static InputStream getCertInput(String certPath) {
        try {
            Resource resource;
            if (certPath.indexOf("cert") == 0) {
                resource = new ClassPathResource(certPath);
            } else if (certPath.indexOf("http") == 0) {
                resource = new UrlResource(certPath);
            } else {
                resource = new FileUrlResource(certPath);
            }
            return resource.getInputStream();
        } catch (IOException e) {
            log.error("加载证书异常：", e);
            throw new ServiceException("加载证书异常");
        }
    }

    /**
     * 营业执照识别
     *
     * @param imgUrl      营业执照图片地址
     * @param accessToken 小程序token
     * @return 识别结果
     */
    public static OcrBizLicenseVo ocrBizLicense(String imgUrl, String accessToken) {
        String url = "https://api.weixin.qq.com/cv/ocr/bizlicense?access_token=" + accessToken + "&img_url=" + imgUrl;
        String post = HttpUtil.post(url, "");
        log.info("微信营业执照识别，请求信息：{}，返回结果：{}", url, post);
        return JsonUtils.parseObject(post, OcrBizLicenseVo.class);
    }

    /**
     * 云闪付商户OCR识别
     *
     * @param imgUrl      营业执照图片地址
     * @param accessToken 小程序token
     * @return 识别结果
     */
    public static OcrBizLicenseVo ocrComm(String imgUrl, String accessToken) {
        String url = "https://api.weixin.qq.com/cv/ocr/comm?access_token=" + accessToken + "&img_url=" + imgUrl;
        String post = HttpUtil.post(url, "");
        log.info("微信ocr识别，请求信息：{}，返回结果：{}", url, post);
        return JsonUtils.parseObject(post, OcrBizLicenseVo.class);
    }

    public static void main(String[] args) throws Exception {
        String accessToken = "76_IZ94Bsb07pcnyS4vAqSiVCRO9gU4RrPhThvnUW-0F0aGw11BsDvgxd2suWJ93JA44rePtBrBdIDSqahjfbWuaNKeLrqjYXEtGC3JFda21bnohAV3WlbufR30sFgZYXfAGAMLK";
//        String imgUrl = "https://discounts-onl.oss-cn-hangzhou.aliyuncs.com/2024/01/08/f13b20a140434e1ca1f4cbd563fdbd5c.jpg";
//        ocrBizLicense(imgUrl, accessToken);
//        String imgUrl = "https://discounts-onl.oss-cn-hangzhou.aliyuncs.com/2024/01/09/d1ab93adc6734f36a8cd773cc83ce3a5.jpg";
//        ocrComm(imgUrl, accessToken);

//        File file = new File("C:\\Users\\25487\\Desktop\\消息推送.xlsx");
////        File file = new File("");
//        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
//            file));
//        List<SendDyInfoVo> hashMaps = ExcelUtil.importExcel(in, SendDyInfoVo.class);
////        List<SendDyInfoVo> hashMaps = new ArrayList<>();
////        SendDyInfoVo sendDyInfoVo = new SendDyInfoVo();
////        sendDyInfoVo.setOpenId("oKrd35bHD8K40_Jfs1jHARweC7TU");
////        hashMaps.add(sendDyInfoVo);
//        log.info("用户信息：{}", hashMaps);
//        Map<String, Object> msgData = new HashMap<>();
//        Map<String, String> thing1 = new HashMap<>();
//        thing1.put("value", "嗨购大连恒隆广场");
//        msgData.put("thing6", thing1);
//
//        Map<String, String> thing7 = new HashMap<>();
//        thing7.put("value", "一元购满500减100优惠券");
//        msgData.put("thing7", thing7);
//
//        Map<String, String> thing10 = new HashMap<>();
//        thing10.put("value", "活动期间可购两张， 数量有限，先到先得");
//        msgData.put("thing10", thing10);
//
//        String accessToken = "75_73_tkBbRnCEefKQ87m5xMvnAItUbLA5-48fEEhMHSSZ36thSli6ER-0ONKlkn1N0OH2377m_-KI7i-udGnv7r37KBMzRsBkHS4_lrlxkcIyl_Y4hlzNK-HGovEEJPTgAEANTX";
//        String templateId = "oF-pemb-OKhpiuAME-FdMmPdqr-6CKuUIC2_1I12ZYA";
////        String page = "pages/index/index";
//        String page = "pages/template/index?templateId=1737396617601662977";
//        for (SendDyInfoVo hashMap : hashMaps) {
//            sendTemplateMessage(accessToken, hashMap.getOpenId(), templateId, page, msgData);
//        }

    }
}
