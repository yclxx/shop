package com.ruoyi.zlyyhmobile.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.RSAUtils;
import com.ruoyi.zlyyh.constant.UnionPayConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 25487
 */
@Slf4j
public class UnionPayUtils {

    /**
     * 组装基本信息
     *
     * @param orderId           订单号
     * @param bizMethod         业务类型
     * @param appId             Appid
     * @param unionPayProductId 产品ID
     * @return JSON
     */
    public static JSONObject getCallbackJson(String orderId, String bizMethod, String appId, String unionPayProductId) {
        JSONObject params = new JSONObject();
        params.put("bizMethod", bizMethod);
        params.put("merId", appId);
        params.put("txnTime", DateUtils.dateTimeNow());
        params.put("orderId", orderId);
        params.put("prodId", unionPayProductId);

        return params;
    }

    /**
     * 通知银联
     *
     * @param params     通知内容
     * @param appId      AppID
     * @param bizMethod  业务类型
     * @param orderId    流水号
     * @param privateKey 私钥
     * @param url        请求地址
     */
    public static void postUnionPay(JSONObject params, String appId, String bizMethod, String orderId, String privateKey, String url) {
        String body = params.toJSONString();
        String str = "version=1.0.0&appId=" + appId + "&bizMethod=" + bizMethod + "&reqId=" + orderId + "&body=" + body;
        String sign = RSAUtils.sign(privateKey, str);
        HttpRequest request = HttpUtil.createPost(url)
            .header(UnionPayConstants.VERSION, "1.0.0")
            .header(UnionPayConstants.APP_TYPE, "02")
            .header(UnionPayConstants.APP_ID, appId)
            .header(UnionPayConstants.BIZ_METHOD, bizMethod)
            .header(UnionPayConstants.SIGN, sign)
            .header(UnionPayConstants.SIGN_METHOD, "RSA2")
            .header(UnionPayConstants.REQ_ID, orderId)
            .body(body);
        HttpResponse execute = request.execute();
        log.info("通知银联分销地址：{},请求信息：{},返回结果：{}", url, request, execute.toString());
    }
}
