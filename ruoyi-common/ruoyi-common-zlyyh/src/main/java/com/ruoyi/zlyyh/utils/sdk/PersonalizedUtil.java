package com.ruoyi.zlyyh.utils.sdk;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class PersonalizedUtil {

    public static void main(String[] args) throws Exception {
        Map<String, String> reqData = new TreeMap<>();
        // 组装HTTP请求报文头
        reqData.put("content-type", "application/json;charset=utf-8");
        reqData.put("Accept", "application/json");
        reqData.put("Accept-Charset", "utf-8");
        reqData.put(PersonalizedSDKConstants.param_bizMethod, "smart.recommenderService");       // 通过bizType.apiId拼接而成
        reqData.put(PersonalizedSDKConstants.param_version, "1.1");                  // 固定填写：1.1
        //reqData.put(PersonalizedSDKConstants.param_appId, "ysf_uc");// 在中台门户登记的服务使用方系统名称英文缩写
        //正式
        reqData.put(PersonalizedSDKConstants.param_appId, "CI000484049");// 在中台门户登记的服务使用方系统名称英文缩写
        long snowflakeNextId = IdUtil.getSnowflakeNextId();
        reqData.put(PersonalizedSDKConstants.param_reqId, String.valueOf(snowflakeNextId));      // 发送方流水号，可以自行定制规则
        reqData.put(PersonalizedSDKConstants.param_reqTs, DateUtils.createTimestampStr(true));

        //reqData.put(PersonalizedSDKConstants.param_signId, "20221123141732856718");     // tokenId，在中台门户系统登记时自动分配的token编号
        //正式
        reqData.put(PersonalizedSDKConstants.param_signId, "20230420095713008332");     // tokenId，在中台门户系统登记时自动分配的token编号
//        reqData.put(PersonalizedSDKConstants.param_token, "20221123141732821300");      // token
        //正式
        reqData.put(PersonalizedSDKConstants.param_token, "20230420095712998460");      // token
        reqData.put(PersonalizedSDKConstants.param_signMethod, "sha256");      // 签名方法，固定填写
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = new HashMap<>();
        Map<String, String> bussType = new HashMap<>();
        Map<String, String> context = new HashMap<>();
        bussType.put(PersonalizedSDKConstants.param_bussFstLvl, "00013310");
        bussType.put(PersonalizedSDKConstants.param_bussSndLvl, "00000001");
        bussType.put(PersonalizedSDKConstants.param_bussTrdLvl, "00000001");

        //context.put(PersonalizedSDKConstants.param_userInfo, "15821856850");
        //context.put(PersonalizedSDKConstants.param_userInfo, "18964659027");
        //context.put(PersonalizedSDKConstants.param_userInfo, "13906520299");
        //context.put(PersonalizedSDKConstants.param_userInfo, "18668011360");
        //context.put(PersonalizedSDKConstants.param_userInfo, "15951812594");
        //context.put(PersonalizedSDKConstants.param_userInfo, "18868832056");
        //context.put(PersonalizedSDKConstants.param_userInfo, "13758168487");
        //context.put(PersonalizedSDKConstants.param_userInfo, "13656637867");
        //context.put(PersonalizedSDKConstants.param_userInfo, "18758073645");
        //context.put(PersonalizedSDKConstants.param_userInfo, "18758165699");
        //context.put(PersonalizedSDKConstants.param_userInfo, "16718721311");
        context.put(PersonalizedSDKConstants.param_userInfo, "16718721311");
        context.put(PersonalizedSDKConstants.param_userTp, "3");
        bodyMap.put(PersonalizedSDKConstants.param_cmd, "rcm_branch_item");


        //        bodyMap.put(PersonalizedSDKConstants.param_appId, "VMrfuyv5Pjn9fEc");
        //正式
        bodyMap.put(PersonalizedSDKConstants.param_appId, "CI000484049");
        bodyMap.put(PersonalizedSDKConstants.param_appPwd, "WkVy3QJm5rRRBVF");
        bodyMap.put(PersonalizedSDKConstants.param_bussType, bussType);
        bodyMap.put(PersonalizedSDKConstants.param_context, context);
        reqData.put(PersonalizedSDKConstants.param_body, JSON.toJSONString(bodyMap));
        Map<String, String> reqMap = signTokenWithSHA256(reqData);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        reqMap.remove(PersonalizedSDKConstants.param_token);
        String url = "http://9.234.58.83:8080/smart/recommenderService";
        //String url = "http://19.126.4.51:8080/smart/recommenderService";
        //String url = "https://172.16.5.166/smart/recommenderService";
        //String url = "https://wechatcounts.yzgnet.com/recommenderService/user/sdkTest";
       // String url = "https://wechatcounts.yzgnet.com/recommenderService/smart/recommenderService";


        HttpRequest body = HttpUtil.createPost(url).addHeaders(reqMap).body(JSON.toJSONString(bodyMap));
        System.out.println(body);

       HttpResponse response = HttpUtil.createPost(url).addHeaders(reqMap).body(JSON.toJSONString(bodyMap)).execute();

        System.out.println(response);

        //        System.out.println(response);
        //这里应该判断请求是否正确

         //组装验签报文

//        verifyMap.put(PersonalizedSDKConstants.param_signId, respHeader.get(PersonalizedSDKConstants.param_signId));
//        verifyMap.put(PersonalizedSDKConstants.param_sign, respHeader.get(PersonalizedSDKConstants.param_sign));
//        verifyMap.put(PersonalizedSDKConstants.param_bizMethod, respHeader.get(PersonalizedSDKConstants.param_bizMethod));
//        verifyMap.put(PersonalizedSDKConstants.param_version, respHeader.get(PersonalizedSDKConstants.param_version));
//        verifyMap.put(PersonalizedSDKConstants.param_signMethod, respHeader.get(PersonalizedSDKConstants.param_signMethod));
//        verifyMap.put(PersonalizedSDKConstants.param_appId, respHeader.get(PersonalizedSDKConstants.param_appId));
//        verifyMap.put(PersonalizedSDKConstants.param_reqId, respHeader.get(PersonalizedSDKConstants.param_reqId));
//        verifyMap.put(PersonalizedSDKConstants.param_reqTs, respHeader.get(PersonalizedSDKConstants.param_reqTs));
//        Map<String, String> verifyMap = new HashMap<>();
//        verifyMap.put(PersonalizedSDKConstants.param_signId, response.header(PersonalizedSDKConstants.param_signId));
//        verifyMap.put(PersonalizedSDKConstants.param_sign, response.header(PersonalizedSDKConstants.param_sign));
//        verifyMap.put(PersonalizedSDKConstants.param_bizMethod, response.header(PersonalizedSDKConstants.param_bizMethod));
//        verifyMap.put(PersonalizedSDKConstants.param_version, response.header(PersonalizedSDKConstants.param_version));
//        verifyMap.put(PersonalizedSDKConstants.param_signMethod, response.header(PersonalizedSDKConstants.param_signMethod));
//        verifyMap.put(PersonalizedSDKConstants.param_appId, response.header(PersonalizedSDKConstants.param_appId));
//        verifyMap.put(PersonalizedSDKConstants.param_reqId, response.header(PersonalizedSDKConstants.param_reqId));
//        verifyMap.put(PersonalizedSDKConstants.param_reqTs, response.header(PersonalizedSDKConstants.param_reqTs));
//        verifyMap.put(PersonalizedSDKConstants.param_token, "20221123141732821300");
//        verifyMap.put(PersonalizedSDKConstants.param_body, response.body());
//
//
//        if (!verifySignTokenWithSHA256(verifyMap)) {
//            log.info("验证银联签名失败\n");
//            return;
//        } else {
//            log.info("验证银联签名成功\n");
//        }
//
//        log.info("从银联获得HTTP应答报文为：" + response.body() + "\n");


    }
    public static Map<String, String> signTokenWithSHA256(Map<String, String> reqData) {
        String toBeSignStr = buildSignString(reqData);
        try {
            String sign = SDKUtil.byteArrayToHexString(sha256(toBeSignStr.getBytes("utf-8")));
            reqData.put("sign", sign);
            return reqData;
        } catch (Exception var3) {
            return null;
        }
    }
    public static String buildSignString(Map<String, String> headers) {
        String bizMethod = headers.get("bizMethod");
        String appId = headers.get("appId");
        String reqTs = headers.get("reqTs");
        String reqId = headers.get("reqId");
        String version = headers.get("version");
        String body = headers.get("body");
        String token = headers.get("token");
        return buildSignString(bizMethod, appId, reqTs, reqId, token, body, version);
    }

    public static boolean verifySignTokenWithSHA256(Map<String, String> respData) {
        String toBeSignStr = buildSignString(respData);
        log.info("待验签报文串：" + toBeSignStr);

        try {
            String vryData = SDKUtil.byteArrayToHexString(sha256(toBeSignStr.getBytes("utf-8")));
            return vryData.equals(respData.get("sign"));
        } catch (Exception var3) {
            log.warn("verify failed. " + var3.getMessage());
            return false;
        }
    }
    public static String buildSignString(String bizMethod, String appId, String reqTs, String reqId, String token, String body, String version) {
        StringBuffer sb = new StringBuffer();
        sb.append("version");
        sb.append("=");
        sb.append(version);
        sb.append("&");
        sb.append("bizMethod");
        sb.append("=");
        sb.append(bizMethod);
        sb.append("&");
        sb.append("appId");
        sb.append("=");
        sb.append(appId);
        sb.append("&");
        sb.append("reqTs");
        sb.append("=");
        sb.append(reqTs);
        sb.append("&");
        sb.append("reqId");
        sb.append("=");
        sb.append(reqId);
        sb.append("&");
        sb.append("token");
        sb.append("=");
        sb.append(token);
        sb.append("&");
        sb.append("body");
        sb.append("=");
        sb.append(body);
        return sb.toString();
    }
    public static byte[] sha256(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes);
            return messageDigest.digest();
        } catch (Exception var2) {
            return null;
        }
    }

}
