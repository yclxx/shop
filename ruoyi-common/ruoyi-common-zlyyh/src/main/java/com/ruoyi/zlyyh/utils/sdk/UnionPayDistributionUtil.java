package com.ruoyi.zlyyh.utils.sdk;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.zlyyh.enumd.UnionPay.UnionPayBizMethod;
import com.ruoyi.zlyyh.properties.utils.YsfDistributionPropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 银联内容分销（渠道方,以【渠道】中国银联内容分销接口规范V0.14-20230320文档为准）
 */
@Slf4j
public class UnionPayDistributionUtil {

    private static Map<String, String> getReqData(String bizMethod, String appId, String reqId) {
        Map<String, String> reqData = new HashMap<>();
        // 组装HTTP请求报文头
        reqData.put("content-type", "application/json;charset=utf-8");
        reqData.put("Accept", "application/json");
        reqData.put("Accept-Charset", "utf-8");
        reqData.put(PersonalizedSDKConstants.param_bizMethod, bizMethod);
        reqData.put(PersonalizedSDKConstants.param_version, "7.0.0");
        // 自定义请求头
        reqData.put("appType", "02");
        // 机构id
        reqData.put(PersonalizedSDKConstants.param_appId, appId);
        // 发送方流水号，可以自行定制规则
        reqData.put(PersonalizedSDKConstants.param_reqId, reqId);
        // 签名方法，固定填写
        reqData.put(PersonalizedSDKConstants.param_signMethod, "SM2");

        return reqData;
    }

    private static Map<String, Object> getBody(String appId, String orderId) {
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = new HashMap<>();
        //正式
        //业务代码 内容ID，固定上送
        bodyMap.put("bussCode", "F2_9800_0000");
        // 渠道商户代码
        bodyMap.put("merId", appId);
        //订单号
        bodyMap.put("orderId", orderId);
        //交易时间
        bodyMap.put("txnTime", DateUtils.dateTimeNow());
        //接入类型
        bodyMap.put("accessType", "0");

        return bodyMap;
    }

    /**
     * 查询商品列表
     */
    public static String selectProductList(Integer curPage, String pagingIndex, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.CHNLPRODLST.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, IdUtil.getSnowflakeNextIdStr());
        //当前页数
        bodyMap.put("curPage", curPage.toString());
        if (curPage > 1) {
            //分页索引,首次分页不上传
            bodyMap.put("pagingIndex", pagingIndex);
        }
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));

        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("商品列表接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("商品列表接口响应数据：{}", result);
        return result;
    }

    /**
     * 查询商品详情
     */
    public static String selectProductDetails(String chnlProdId, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.PRODDTL.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, IdUtil.getSnowflakeNextIdStr());
        //渠道商品代码
        bodyMap.put("chnlProdId", chnlProdId);
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));

        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("商品详情接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("商品详情接口响应数据：{}", result);
        return result;
    }

    /**
     * 创建订单(我方暂时无任何活动，chnlPayAmt默认为0，如有需要再修改)
     *
     * @param chnlProdId 商品id
     * @param purQty     购买数量
     * @param orderAmt   订单金额
     * @param usrPayAmt  用户支付金额
     */
    public static String createOrder(Long number, String chnlProdId,
                                     Long purQty, String orderAmt, String usrPayAmt, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.CREATE.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, number.toString());
        //渠道商品代码
        bodyMap.put("chnlProdId", chnlProdId);
        //购买数量
        bodyMap.put("purQty", purQty);
        //订单金额
        bodyMap.put("orderAmt", orderAmt);
        //用户支付金额
        bodyMap.put("usrPayAmt", usrPayAmt);
        //渠道抵扣金额
        bodyMap.put("chnlPayAmt", 0);
        //渠道用户 id
        bodyMap.put("chnlUsrId", appId);
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));

        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("创建订单接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("创建订单接口响应数据：{}", result);
        return result;
    }

    /**
     * 订单支付（前台）
     */
    public static String orderPay(Long number, String prodTn, String usrPayAmt, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.FRONT.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, number.toString());
        // 创建订单时，返回的商品订单号
        bodyMap.put("prodTn", prodTn);
        // 交易币种
        bodyMap.put("currencyCode", 156);
        // 用户支付金额
        bodyMap.put("usrPayAmt", usrPayAmt);
        // 支付类型
        bodyMap.put("billPayType", "3");
        // 后台通知地址
        bodyMap.put("backUrl", YsfDistributionPropertiesUtils.getBackUrl(platformKey));
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));
        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("订单支付接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("订单支付接口响应数据：{}", result);
        return result;
    }

    /**
     * 订单取消
     */
    public static void orderCancel(Long number, String prodTn, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.CANCEL.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, number.toString());
        // 创建订单时，返回的商品订单号
        bodyMap.put("prodTn", prodTn);
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));

        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("订单取消接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("订单取消接口响应数据：{}", result);
    }

    /**
     * 订单发券 （直销调用，可根据需求自行调用）
     */
    public static String orderSend(Long number, String chnlProdId, String prodTn, String prodAstIdTp, String prodAstId, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.chnlpur.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, number.toString());
        // 创建订单时，返回的商品订单号
        bodyMap.put("prodTn", prodTn);
        bodyMap.put("chnlAppid", appId);
        bodyMap.put("chnlProdId", chnlProdId);
        bodyMap.put("prodAstIdTp", prodAstIdTp);
        bodyMap.put("prodAstId", prodAstId);
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));

        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("订单发券接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("订单发券接口响应数据：{}", result);
        return result;
    }

    /**
     * 发券 （代销调用，可根据需求自行调用）
     */
    public static String send(Long number, String chnlProdId, String purQty, String prodAstIdTp, String prodAstId, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.CHNLPUR.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, number.toString());
        bodyMap.put("chnlAppid", appId);
        bodyMap.put("chnlProdId", chnlProdId);
        bodyMap.put("purQty", purQty);
        bodyMap.put("prodAstIdTp", prodAstIdTp);
        bodyMap.put("prodAstId", prodAstId);
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));

        // 签名
        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("发券接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("发券接口响应数据：{}", result);
        return result;
    }

    /**
     * 订单状态查询
     */
    public static String orderStatus(String bizMethod, String chnlProdId, Long origOrderId, String origTxnTime, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.STQUERY.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, IdUtil.getSnowflakeNextIdStr());
        // 商品代码
        bodyMap.put("chnlProdId", chnlProdId);
        // 原交易关键字
        bodyMap.put("origCmd", bizMethod);
        // 原交易订单号
        bodyMap.put("origOrderId", origOrderId);
        // 原交易时间
        bodyMap.put("origTxnTime", origTxnTime);
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));

        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("订单状态查询接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("订单状态查询接口响应数据：{}", result);
        return result;
    }

    /**
     * 退券
     */
    public static String orderRefundCoupon(Long number, String chnlProdId, String prodTn, String bondSerlNo, Long platformKey, String appId, String certPath) {
        Map<String, String> reqData = getReqData(UnionPayBizMethod.CHNLRFD.getBizMethod(), appId, IdUtil.getSnowflakeNextIdStr());
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = getBody(appId, number.toString());
        bodyMap.put("chnlProdId", chnlProdId);
        bodyMap.put("prodTn", prodTn);
        bodyMap.put("bondSerlNo", bondSerlNo);
        reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));
        // 签名
        Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, certPath, YsfDistributionPropertiesUtils.getCertPwd(platformKey));
        log.info("订单退券接口请求数据:{}", reqMap);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        String result = HttpUtil.createPost(YsfDistributionPropertiesUtils.getUrl(platformKey)).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute().body();
        log.info("订单退券接口响应数据：{}", result);
        return result;
    }

//    public static void main(String[] args) {
//        //status();
//    }

    //public static void status() {
    //    String url = "https://sfpay.95516.com/ucp";
    //    String appId = "8983199799707JD";
    //    HashMap<String, String> reqData = new HashMap<>();
    //    // 组装HTTP请求报文头
    //    reqData.put("content-type", "application/json;charset=utf-8");
    //    reqData.put("Accept", "application/json");
    //    reqData.put("Accept-Charset", "utf-8");
    //    reqData.put(PersonalizedSDKConstants.param_bizMethod, UnionPayBizMethod.STQUERY.getBizMethod());
    //    reqData.put(PersonalizedSDKConstants.param_version, "7.0.0");
    //    // 自定义请求头
    //    reqData.put("appType", "02");
    //    reqData.put(PersonalizedSDKConstants.param_appId, appId);// 机构id
    //    reqData.put(PersonalizedSDKConstants.param_reqId, IdUtil.getSnowflakeNextIdStr());// 发送方流水号，可以自行定制规则
    //    reqData.put(PersonalizedSDKConstants.param_signMethod, "SM2");// 签名方法，固定填写
    //
    //    // 组装HTTP请求报文体 根据实际业务填写
    //    Map<String, Object> bodyMap = new HashMap<>();
    //    //正式
    //    bodyMap.put("bussCode", "F2_9800_0000"); //业务代码 内容ID，固定上送
    //    bodyMap.put("merId", appId);// 渠道商户代码
    //    bodyMap.put("orderId", IdUtil.getSnowflakeNextIdStr()); //订单号
    //    bodyMap.put("txnTime", DateUtils.dateTimeNow()); //交易时间
    //    bodyMap.put("accessType", "0");
    //    //bodyMap.put("chnlProdId", "N000000448"); // 原交易关键字
    //    bodyMap.put("origCmd", "ma.ids.order.pay.chnlpur"); // 原交易关键字
    //    bodyMap.put("origOrderId", "1690921006154981376");// 原交易订单号
    //    bodyMap.put("origTxnTime", "20230814105918");// 原交易时间
    //    reqData.put(PersonalizedSDKConstants.param_body, JsonUtils.toJsonString(bodyMap));
    //    Map<String, String> reqMap = UnionPayMerchantUtil.sign(reqData, "cert/8983199799707JD.sm2", "123123");
    //    log.info("订单状态查询接口请求数据:{}", reqMap);
    //    reqMap.remove(PersonalizedSDKConstants.param_body);
    //    HttpResponse response = HttpUtil.createPost(url).addHeaders(reqMap).body(JsonUtils.toJsonString(bodyMap)).execute();
    //    log.info("订单状态查询接口响应数据：{}", JsonUtils.toJsonString(JSON.parse(response.body())));
    //}
}
