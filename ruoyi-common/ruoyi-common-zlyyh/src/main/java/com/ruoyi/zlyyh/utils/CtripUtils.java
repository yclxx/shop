package com.ruoyi.zlyyh.utils;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.zlyyh.domain.Order;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 携程套餐相关接口帮助类
 *
 * @author 25487
 */
@Slf4j
public class CtripUtils {
    /**
     * 查询门店列表
     *
     */
    public static JSONObject getShopList(Integer pageSize, Integer pageNum, String partnerType,String url) {
        //请求参数
        Map<String,Object> map = new HashMap<>();
        map.put("pageIndex",pageNum);
        map.put("pageSize",pageSize);
        Map<String,Object> map1 = new HashMap<>();
        map1.put("partnerType",partnerType);
        map.put("header",map1);
        String paramJson =JSONObject.toJSONString(map);

        String result = HttpUtil.createPost(url).body(paramJson).execute().body();
        if(ObjectUtil.isNull(result)){
            log.error("携程获取商品列表，返回空！");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject resultJson = jsonObject.getJSONObject("result");
        int code = resultJson.getIntValue("code");
        if(0 != code){
            log.error("携程获取美食商品失败，失败原因：{}",resultJson.getString("message"));
            return null;
        }
        return jsonObject;
    }


    /**
     * 查询门店详情列表
     *
     */
    public static JSONObject getShopProductList(String partnerType, String poiId,String url) {
        //请求参数
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("partnerType",partnerType);
        map.put("header",map1);
        map.put("poiId",poiId);
        String paramJson =JSONObject.toJSONString(map);
        String result = HttpUtil.createPost(url).body(paramJson).execute().body();
        if(ObjectUtil.isNull(result)){
            log.error("携程获取门店商品详情，返回空！");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject resultJson = jsonObject.getJSONObject("result");
        int code = resultJson.getIntValue("code");
        if(0 != code){
            log.error("携程获取门店商品失败，失败原因：{}",resultJson.getString("message"));
            return null;
        }
        return jsonObject;
    }


    /**
     * 查询门店详情列表
     *
     */
    public static JSONObject getProductInfo(String partnerType, String productId,String url) {
        //请求参数
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("partnerType",partnerType);
        map.put("header",map1);
        map.put("productId",productId);
        String paramJson =JSONObject.toJSONString(map);
        String result = HttpUtil.createPost(url).body(paramJson).execute().body();
        if(ObjectUtil.isNull(result)){
            log.error("携程获取商品详情，返回空！");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject resultJson = jsonObject.getJSONObject("result");
        int code = resultJson.getIntValue("code");
        if(0 != code){
            log.error("携程获取商品失败，失败原因：{}",resultJson.getString("message"));
            return null;
        }
        return jsonObject;
    }


    /**
     * 携程预下单
     *
     */
    public static JSONObject createCtripOrder(Order order,String mobile ,String partnerType, String productId, String url) {
        //请求参数
        //创建携程订单
        Map<String, Object> map = new HashMap<>();
        map.put("sourceSerialNumber", order.getNumber().toString());
        map.put("productId",productId);
        map.put("quantity", 1);
        map.put("countryCode", "086");
        map.put("userMobile", mobile);
        map.put("userName", "匿名");
        Map<String, Object> map1 = new HashMap<>();
        map1.put("partnerType", partnerType);
        map.put("header", map1);
        String paramJson = JSONObject.toJSONString(map);
        String result = HttpUtil.createPost(url).body(paramJson).execute().body();
        if (ObjectUtil.isEmpty(result)) {
            log.error("订单编号：{}下单失败，请求接口返回空", order.getNumber());
            throw new ServiceException("系统繁忙，请稍后重试！");
        }
        return JSONObject.parseObject(result);
    }


    /**
     * 携程支付订单
     *
     */
    public static JSONObject payCtripOrder(String ctripOrderId,String partnerType,String url) {
        //请求参数
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", ctripOrderId);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("partnerType", partnerType);
        map.put("header", map1);
        String paramJson = JSONObject.toJSONString(map);
        String result = HttpUtil.createPost(url).body(paramJson).execute().body();
        log.info("携程订单编号：{}，携程确认订单接口返回参数：{}", ctripOrderId, result);
        if (ObjectUtil.isEmpty(result)) {
            log.error("携程订单编号：{}，确认订单接口返回null", ctripOrderId);
            throw new ServiceException("系统繁忙，请稍后重试!");
        }
        return JSONObject.parseObject(result);
    }

    public static String cancelOrder(String externalOrderNumber,String partnerType,String refundUrl) {
// 取消订单请求参数
        Map<String,Object> map = new HashMap<>();
        map.put("orderId",externalOrderNumber);
        map.put("codes",new ArrayList<>());
        Map<String,Object> map1 = new HashMap<>();
        map1.put("partnerType", partnerType);
        map.put("header",map1);
        String paramJson = JSONObject.toJSONString(map);
        String result = HttpUtil.createPost(refundUrl).body(paramJson).execute().body();
        if(ObjectUtil.isEmpty(result)){
            log.error("携程取消订单接口返回null");
            throw new ServiceException("系统繁忙，请稍后重试！");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject resultJson = jsonObject.getJSONObject("result");
        String code = resultJson.getString("code");
        if(!"0".equals(code)){
            String message = resultJson.getString("message");
            log.error("携程取消订单失败，携程订单编号：{}，失败原因：{}",externalOrderNumber,message);
            throw new ServiceException("系统繁忙，请稍后重试！");
        }
        return code;

    }



    public static void main(String[] args) {
        String accessToken = getAccessToken();
        //请求参数
//        Map<String,Object> map = new HashMap<>();
//        Map<String,Object> map1 = new HashMap<>();
//        map1.put("partnerType","Yunshanfu");
//        map.put("header",map1);
//        map.put("pageIndex",1);
//        map.put("pageSize",20);
//        String param1 = JSONObject.toJSONString(map);
//        String url = "https://sopenservice.ctrip.com/openservice/serviceproxy.ashx?AID=3231970&SID=7645764&ICODE=af99f2f7f57e458fa764629f1342368e&Token="+accessToken;
//        JSONObject yunshanfu = getShopList(20, 1, "Yunshanfu", url);
//        System.out.println(yunshanfu);
//        String shopinfoUrl = "https://sopenservice.ctrip.com/openservice/serviceproxy.ashx?AID=3231970&SID=7645764&ICODE=992dd15221df48b6a0b598da026405ce&Token="+accessToken;
//        System.out.println(getShopProductList("Yunshanfu", "130152272", shopinfoUrl));
//        String proinfoUrl = "https://sopenservice.ctrip.com/openservice/serviceproxy.ashx?AID=3231970&SID=7645764&ICODE=8085b3470a3e463cab89a819668b688d&Token="+accessToken;
//        System.out.println(getProductInfo("Yunshanfu", "63558", proinfoUrl));
//        String shopinfUrl = "https://sopenservice.ctrip.com/openservice/serviceproxy.ashx?AID=3231970&SID=7645764&ICODE=eba2196988674a65991c6150d03dca76&Token="+accessToken;
//        System.out.println(getShopProductList("Yunshanfu", "130152272", shopinfUrl));
//        Order order = new Order();
//        order.setNumber(93882172937432L);
//        System.out.println(createCtripOrder(order, "18757906428", "Yunshanfu", "115006", "https://sopenservice.ctrip.com/openservice/serviceproxy.ashx?AID=3231970&SID=7645764&ICODE=7056c065d68c46bd8ba9555fd2e323e8&Token=" + accessToken));

    }


    /**
     * 初始化Access token
     */
    public static synchronized String getAccessToken() {
        String url = "https://sopenservice.ctrip.com/openserviceauth/authorize.ashx";
        String param = "AID=" + "3231970" + "&SID=" + "7645764" + "&KEY=" + "6a26d37eaccb4abaa734435f08f4ed50";
        String result = HttpUtil.createGet(url).body(param).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject);

        return jsonObject.getString("Access_Token");
    }



}
