//package com.ruoyi.zlyyhmobile.controller;
//
//import cn.hutool.core.util.ObjectUtil;
//import com.ruoyi.common.core.utils.JsonUtils;
//import com.ruoyi.common.core.utils.RSAUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.protocol.HTTP;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 银联分销内容方控制器
// * 前端访问路由地址为:/zlyyh-mobile/unionPay
// *
// * @author yzg
// * @date 2023-09-15
// */
//@Slf4j
//@Validated
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/unionPay")
//public class UnionPayContentController {
//
//    /**
//     * 内容分销 发券
//     */
//    @PostMapping("/send")
//    public void distributionSend(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        // 获取请求头相关数据
//        String version = request.getHeader("version");
//        String appId = request.getHeader("appId");
//        String bizMethod = request.getHeader("bizMethod");
//        String sign = request.getHeader("sign");
//        String signMethod = request.getHeader("signMethod");
//        String reqId = request.getHeader("reqId");
//        String body = getPostData(request);
//        log.info("内容分销订单发券，请求头：{}，请求参数：{}", getHeaderData(request), body);
//
//        // 准备响应头数据
//        response.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
//        response.setHeader("version", version);
//        response.setHeader("bizMethod", bizMethod);
//        response.setHeader("reqId", reqId);
//        response.setHeader("signMethod", signMethod);
//        // 准备验证数据签名
//        String value = "version=" + version + "&appId=" + appId + "&bizMethod=" + bizMethod + "&reqId=" + reqId + "&body=";
//
//        // 获取分销商信息，并判断分销商数据
//        Distributor distributor = distributorService.queryByAppId(appId);
//        if (ObjectUtil.isEmpty(distributor)) {
//            String code = "{\"code\":\"40030000\",\"msg\":\"此商户号异常\"}";
//            String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//            response.setHeader("sign", resultSign);
//            response.getWriter().write(code);
//            log.info("发券响应数据：" + code);
//            return;
//        }
//
//        // 判断请求数据是否异常
//        if (body == null) {
//            String code = "{\"code\":\"40030000\",\"msg\":\"请求数据异常\"}";
//            response.getWriter().write(code);
//            log.info("发券响应数据：" + code);
//            return;
//        }
//
//        // 判断算法算法数据
//        if (signMethod.equals("RSA2")) {
//            boolean verifySign = RSAUtils.verifySign(distributor.getPublicKey(), value + body, sign);
//            if (!verifySign) {
//                String code = "{\"code\":\"40030000\",\"msg\":\"sign验证失败\"}";
//                String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//                response.setHeader("sign", resultSign);
//                response.getWriter().write(code);
//                log.info("发券响应数据：" + code);
//                return;
//            }
//        } else {
//            String code = "{\"code\":\"40030000\",\"msg\":\"请使用RAS2算法\"}";
//            String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//            response.setHeader("sign", resultSign);
//            response.getWriter().write(code);
//            log.info("发券响应数据：" + code);
//            return;
//        }
//
//        // 将获取的请求JSON转为Map类型
//        Map<String, Object> paramsMap = JsonUtils.parseMap(body);
//        // 开始执行发券操作，并返回执行结果
//        String result = orderService.createOrder(distributor, paramsMap.get("txnTime").toString(),
//            paramsMap.get("orderId").toString(), bizMethod, paramsMap.get("prodId").toString(),
//            paramsMap.get("purQty").toString(), paramsMap.get("prodAstIdTp").toString(),
//            paramsMap.get("prodAstId").toString());
//
//        // 准备签名
//        String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + result);
//        response.setHeader("sign", resultSign);
//        response.getWriter().write(result);
//        log.info("发券响应数据：" + result);
//    }
//
//    /**
//     * 退券
//     *
//     * @param request  http请求
//     * @param response http响应
//     */
//    @RequestMapping("/refund")
//    public void distributionRefund(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        // 获取请求头相关数据
//        String version = request.getHeader("version");
//        String appId = request.getHeader("appId");
//        String bizMethod = request.getHeader("bizMethod");
//        String sign = request.getHeader("sign");
//        String signMethod = request.getHeader("signMethod");
//        String reqId = request.getHeader("reqId");
//        String body = getPostData(request);
//        log.info("退券请求数据：" + body);
//
//        // 准备响应头数据
//        response.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
//        response.setHeader("version", version);
//        response.setHeader("bizMethod", bizMethod);
//        response.setHeader("reqId", reqId);
//        response.setHeader("signMethod", signMethod);
//        // 准备验证数据签名
//        String value = "version=" + version + "&appId=" + appId + "&bizMethod=" + bizMethod + "&reqId=" + reqId + "&body=";
//
//        // 获取分销商信息，并判断分销商数据
//        Distributor distributor = distributorService.queryByAppId(appId);
//        if (ObjectUtil.isEmpty(distributor)) {
//            String code = "{\"code\":\"40030000\",\"msg\":\"此商户号异常\"}";
//            String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//            response.setHeader("sign", resultSign);
//            response.getWriter().write(code);
//            log.info("退券响应数据：" + code);
//            return;
//        }
//
//        // 判断请求数据是否异常
//        if (body == null) {
//            String code = "{\"code\":\"40030000\",\"msg\":\"请求数据异常\"}";
//            response.getWriter().write(code);
//            log.info("退券响应数据：" + code);
//            return;
//        }
//
//        // 判断算法算法数据
//        if (signMethod.equals("RSA2")) {
//            Boolean verifySign = RSAUtils.verifySign(distributor.getPublicKey(), value + body, sign);
//            if (!verifySign) {
//                String code = "{\"code\":\"40030000\",\"msg\":\"sign验证失败\"}";
//                String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//                response.setHeader("sign", resultSign);
//                response.getWriter().write(code);
//                log.info("退券响应数据：" + code);
//                return;
//            }
//        } else {
//            String code = "{\"code\":\"40030000\",\"msg\":\"请使用RAS2算法\"}";
//            String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//            response.setHeader("sign", resultSign);
//            response.getWriter().write(code);
//            log.info("退券响应数据：" + code);
//            return;
//        }
//
//        Map<String, Object> paramsMap = JsonUtils.parseMap(body);
//        String result = orderService.refundOrder(distributor, paramsMap.get("bizMethod").toString(), paramsMap.get("txnTime").toString(),
//            paramsMap.get("orderId").toString(), paramsMap.get("prodId").toString(), paramsMap.get("bondNo").toString());
//
//        String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + result);
//        response.setHeader("sign", resultSign);
//        response.getWriter().write(result);
//        log.info("退券响应数据：" + result);
//    }
//
//    /**
//     * 交易状态查询
//     *
//     * @param request
//     * @param response
//     * @throws Exception
//     */
//    @RequestMapping("/dealStatus")
//    public void dealStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        // 获取请求头相关数据
//        String version = request.getHeader("version");
//        //String appType = request.getHeader("appType");
//        String appId = request.getHeader("appId");
//        String bizMethod = request.getHeader("bizMethod");
//        String sign = request.getHeader("sign");
//        String signMethod = request.getHeader("signMethod");
//        String reqId = request.getHeader("reqId");
//        String body = getPostData(request);
//        log.info("交易状态请求数据：" + body);
//
//        // 准备响应头数据
//        response.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
//        response.setHeader("version", version);
//        response.setHeader("bizMethod", bizMethod);
//        response.setHeader("reqId", reqId);
//        response.setHeader("signMethod", signMethod);
//        // 准备验证数据签名
//        String value = "version=" + version + "&appId=" + appId + "&bizMethod=" + bizMethod + "&reqId=" + reqId + "&body=";
//
//        // 获取分销商信息，并判断分销商数据
//        Distributor distributor = distributorService.queryByAppId(appId);
//        if (ObjectUtil.isEmpty(distributor)) {
//            String code = "{\"code\":\"40030000\",\"msg\":\"此商户号异常\"}";
//            String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//            response.setHeader("sign", resultSign);
//            response.getWriter().write(code);
//            log.info("交易状态响应数据：" + code);
//            return;
//        }
//
//        // 判断请求数据是否异常
//        if (body == null) {
//            String code = "{\"code\":\"40030000\",\"msg\":\"请求数据异常\"}";
//            response.getWriter().write(code);
//            log.info("交易状态响应数据：" + code);
//            return;
//        }
//
//        // 判断算法算法数据
//        if (signMethod.equals("RSA2")) {
//            Boolean verifySign = RSAUtils.verifySign(distributor.getPublicKey(), value + body, sign);
//            if (!verifySign) {
//                String code = "{\"code\":\"40030000\",\"msg\":\"sign验证失败\"}";
//                String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//                response.setHeader("sign", resultSign);
//                response.getWriter().write(code);
//                log.info("交易状态响应数据：" + code);
//                return;
//            }
//        } else {
//            String code = "{\"code\":\"40030000\",\"msg\":\"请使用RAS2算法\"}";
//            String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + code);
//            response.setHeader("sign", resultSign);
//            response.getWriter().write(code);
//            log.info("交易状态响应数据：" + code);
//            return;
//        }
//
//        Map<String, Object> paramsMap = JsonUtils.parseMap(body);
//        String result = orderService.dealStatus(distributor, paramsMap.get("bizMethod").toString(), paramsMap.get("txnTime").toString(), paramsMap.get("orderId").toString(),
//            paramsMap.get("origBizMethod").toString(), paramsMap.get("origTxnTime").toString(), paramsMap.get("origOrderId").toString());
//
//        String resultSign = RSAUtils.sign(distributor.getPrivateKey(), value + result);
//        response.setHeader("sign", resultSign);
//        response.getWriter().write(result);
//        log.info("交易状态响应数据：" + result);
//    }
//
//    /**
//     * 获取Post请求体的全部参数
//     *
//     * @param request 请求数据
//     * @return request请求体参数
//     */
//    private String getPostData(HttpServletRequest request) {
//        try {
//            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
//            StringBuilder responseStrBuilder = new StringBuilder();
//            String inputStr;
//            while ((inputStr = streamReader.readLine()) != null) {
//                responseStrBuilder.append(inputStr);
//            }
//            Map<String, Object> map = JsonUtils.parseMap(responseStrBuilder.toString());
//            return JsonUtils.toJsonString(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 获取Post请求体的全部参数
//     *
//     * @param request 请求数据
//     * @return request请求体参数
//     */
//    private Map<String, String> getHeaderData(HttpServletRequest request) {
//        try {
//            Map<String, String> reqHeader = new HashMap<>();
//            Enumeration er = request.getHeaderNames();//获取请求头的所有name值
//            while (er.hasMoreElements()) {
//                String name = (String) er.nextElement();
//                String value = request.getHeader(name);
//                reqHeader.put(name, value);
//            }
//            return reqHeader;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
