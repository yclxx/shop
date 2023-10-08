package com.ruoyi.zlyyh.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.param.LianLianParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.Collections;

@Slf4j
public class LianLianUtils {
    /*
     * 数据加密 encryptedData
     * */
    public static String aesEncrypt(String data, String secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // 加密算法/ 工作模式/填充方式
            byte[] dataBytes = data.getBytes("UTF-8");
            cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(Base64Utils.decodeFromString(secretKey), "AES"));
            byte[] result = cipher.doFinal(dataBytes);
            return Base64Utils.encodeToString(result);
        } catch (Exception e) {
            log.error("执行 CodecUtil.aesEncrypt 失败：data={}，异常：{}", data, e);
        }
        return null;
    }

    /*
     * 数据解密 encryptedData
     * */
    public static String aesDecrypt(String encryptedDataBase64, String securityKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // 加密算法/工作模式/填充方式
            byte[] dataBytes = Base64Utils.decodeFromString(encryptedDataBase64);
            cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(Base64Utils.decodeFromString(securityKey), "AES"));
            byte[] result = cipher.doFinal(dataBytes);
            return new String(result);
        } catch (Exception e) {
            log.error("执行 aesDecrypt 失败：data={}，异常：{}", encryptedDataBase64, e);
            new ServiceException("");
        }
        return null;
    }

    public static String aesDecryptBack(JSONObject param, String security) {
        LianLianParam lianLianParam = JSONObject.toJavaObject(param.getJSONObject("data"), LianLianParam.class);
        //获取加密数据
        String encryptedData = lianLianParam.getEncryptedData();
        String sign = Md5Utils.encrypt(encryptedData + lianLianParam.getTimestamp().toString());
        if (sign.equals(lianLianParam.getSign())) {
            //校验签名通过,解密 encryptedData
            encryptedData = LianLianUtils.aesDecrypt(encryptedData, security);
            return encryptedData;
        }
        return null;
    }


    /**
     * 查询产品列表
     *
     * @return
     */
    public static JSONObject getProductList(String channelId, String secret, String url, String cityCode, Integer pageNum) {
        //组装产品列表请求参数
        LianLianParam.ProductListParam productListParam = new LianLianParam.ProductListParam();
        productListParam.setChannelId(channelId);
        productListParam.setCityCode(cityCode);
        productListParam.setPageNum(pageNum);
        productListParam.setPageSize(10);
        String encryptedData = JSONObject.toJSONString(productListParam);
        return sendLianLianHttp(channelId, secret, url, encryptedData, true);
    }

    /**
     * 请求联联接口 查询产品图文详情(文案)
     *
     * @param productId 产品ID
     */
    public static JSONObject getProductDetail(String channelId, String secret, String url, String productId) {
        //组装产品详情请求参数
        LianLianParam.ProductDetailParam productDetailParam = new LianLianParam.ProductDetailParam();
        productDetailParam.setProductId(productId);
        String encryptedData = JSONObject.toJSONString(productDetailParam);
        return sendLianLianHttp(channelId, secret, url, encryptedData, true);
    }

    /**
     * 查询产品详情信息
     *
     * @param productId
     * @return
     */
    public static JSONObject getProductShop(String channelId, String secret, String url, String productId) {
        //组装产品列表请求参数
        LianLianParam.ProductInfoParam productInfoParam = new LianLianParam.ProductInfoParam();
        productInfoParam.setChannelId(channelId);
        productInfoParam.setProductId(productId);
        String encryptedData = JSONObject.toJSONString(productInfoParam);
        return sendLianLianHttp(channelId, secret, url, encryptedData, true);
    }

    /**
     * 验证-渠道订单创建条件
     *
     * @param productId 联联商品id
     * @param itemId    联联套餐id
     * @return
     */
    public static JSONObject getValidToken(String channelId, String secret, String url, String number, BigDecimal settlePrice,
                                           String productId, String itemId, String customerPhoneNumber) {
        LianLianParam.CheckOrderParam checkOrderParam = new LianLianParam.CheckOrderParam();
        checkOrderParam.setThirdPartyOrderNo(number);
        checkOrderParam.setProductId(Integer.valueOf(productId));
        checkOrderParam.setItemId(itemId);//套餐id
        checkOrderParam.setCustomerPhoneNumber(customerPhoneNumber);
        checkOrderParam.setQuantity(1);
        checkOrderParam.setPayType(1);
        checkOrderParam.setLocationId("1");
        checkOrderParam.setCustomerName("匿名");
        checkOrderParam.setPurchaseTime(DateUtils.getTime());
        checkOrderParam.setSettlePrice(settlePrice.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString());
        String encryptedData = JSONObject.toJSONString(checkOrderParam);
        return sendLianLianHttp(channelId, secret, url, encryptedData, true);
    }

    /**
     * 创建订单
     *
     * @param productId 联联商品id
     * @param itemId    联联套餐id
     * @return
     */
    public static JSONObject createOrder(String channelId, String secret, String url, String number, BigDecimal settlePrice,
                                         String validToken, String productId, String itemId, String customerPhoneNumber) {
        LianLianParam.CreateOrderParam createOrderParam = new LianLianParam.CreateOrderParam();
        createOrderParam.setValidToken(validToken);
        createOrderParam.setThirdPartyOrderNo(number);
        createOrderParam.setProductId(Integer.valueOf(productId));
        createOrderParam.setItemId(itemId);
        createOrderParam.setLocationId("1");
        createOrderParam.setCustomerName("匿名");
        createOrderParam.setPurchaseTime(DateUtils.getTime());
        createOrderParam.setSettlePrice(settlePrice.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString());
        createOrderParam.setCustomerPhoneNumber(customerPhoneNumber);
        createOrderParam.setQuantity(1);
        createOrderParam.setPayType(1);//余额
        String encryptedData = JSONObject.toJSONString(createOrderParam);
        return sendLianLianHttp(channelId, secret, url, encryptedData, true);
    }

    /**
     * 查询订单详情
     */
    public static JSONObject getOrderDetails(String channelId, String secret, String url, String number, String channelOrderId) {
        LianLianParam.OrderQueryParam queryParam = new LianLianParam.OrderQueryParam();
        queryParam.setThirdOrderId(number);//第三方订单号
        queryParam.setChannelOrderId(channelOrderId);//渠道订单号
        queryParam.setChannelId(channelId);
        String encryptedData = JSONObject.toJSONString(queryParam);
        return sendLianLianHttp(channelId, secret, url, encryptedData, true);
    }

    /**
     * 订单退款接口
     */
    public static JSONObject refund(String channelId, String secret, String url, String channelOrderId, String orderId) {
        LianLianParam.ApplyRefundParam applyRefundParam = new LianLianParam.ApplyRefundParam();
        applyRefundParam.setChannelOrderNo(channelOrderId);//渠道订单号
        applyRefundParam.setOrderNoList(Collections.singletonList(orderId));//子订单号
        String encryptedData = JSONObject.toJSONString(applyRefundParam);
        return sendLianLianHttp(channelId, secret, url, encryptedData, true);
    }

    public static JSONObject sendLianLianHttp(String channelId, String secret, String url, String encryptedData, boolean showLog) {
        if (showLog) {
            log.info("联联接口请求参数，api=>{},encryptedData=>{}", url, encryptedData);
        }
        String result = httpSend(channelId, secret, url, encryptedData);
        if (!StringUtils.isEmpty(result)) {
            // 格式化result
            JSONObject jsonObject = JSONObject.parseObject(result);
            if ("200".equals(jsonObject.getString("code"))) { // 请求成功
                JSONObject data = jsonObject.getJSONObject("data");
                if (data != null) {
                    // 所有的返回数据都在 encryptedData 或者 securityData
                    encryptedData = data.getString("encryptedData");
                    if (StringUtils.isEmpty(encryptedData)) {
                        // 如果 encryptedData 为空，则从securityData中拿加密的数据
                        encryptedData = data.getString("securityData");
                    }
                    // 对encryptedData进行解密
                    encryptedData = aesDecrypt(encryptedData, secret);
                    if (showLog) {
                        log.info("联联接口返回结果解密后数据 => {}", encryptedData);
                    }
                    // 解密后的数据
                    return JSON.parseObject(encryptedData);
                }
            }
            log.error("联联接口请求失败，api=>{},encryptedData=>{},result=>{}", url, encryptedData, result);
        }
        return null;
    }

    private static String httpSend(String channelId, String secret, String url, String encryptedData) {
        //加密encryptedData
        encryptedData = aesEncrypt(encryptedData, secret);
        if (encryptedData == null) {
            return null;
        }
        Long timestamp = System.currentTimeMillis();
        LianLianParam param = LianLianParam.builder()
            .channelId(channelId)
            .encryptedData(encryptedData)
            .timestamp(timestamp)
            .sign(Md5Utils.encrypt(encryptedData + channelId + timestamp))
            .build();
        return HttpUtil.createPost(url).body(JSONObject.toJSONString(param)).execute().body();
    }

    public static void main(String[] args) {
        // 账号
        String channelId = "10018";
        // 域名
        String secret = "Ab7DHJ5vF4jtnvkUxXjIwQ==";
        // 地址
        String basePath = "https://adapter-channel.llzby.top";

        String api = "/ll/channel/order/getOrderInfo";

        JSONObject returnLog = getOrderDetails(channelId, secret, basePath + api, "1710576264179134464", "");
        log.info("订单详情展示：{}", returnLog);
    }
}
