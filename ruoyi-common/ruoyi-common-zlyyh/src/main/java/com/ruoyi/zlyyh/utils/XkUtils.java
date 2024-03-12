package com.ruoyi.zlyyh.utils;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 享库套餐相关接口帮助类
 *
 * @author 25487
 */
@Slf4j
public class XkUtils {
    /**
     * 享库获取商品列表
     * @return
     */
    public static JSONObject getProductList(Integer page,Integer pageSize,String url,String appId,String appSecret,String sourceType){
        Map<String, Object> map = new HashMap<>();
        long timeMillis = System.currentTimeMillis();
        map.put("page", page);
        map.put("list_rows", pageSize);
        map.put("sourceType", sourceType);
        map.put("appid", appId);
        map.put("appsecret", appSecret);
        map.put("timestamp", timeMillis);
        String sign = "appid=" + appId + "&appsecret=" + appSecret + "&timestamp=" + timeMillis;
        sign = DigestUtils.md5Hex(sign.toUpperCase());
        map.put("sign", sign);
        url = url + "?s=openapiGoodsList";
        String s = null;
        try {
            s = HttpUtil.createPost(url).body(JSONObject.toJSONString(map)).setReadTimeout(60000).execute().body();
        } catch (Exception e) {
            log.error("新享库获取商品列表出现异常，异常信息：{}", e.getMessage());
        }
        if(ObjectUtil.isNull(s)){
            log.error("新享库获取商品列表，返回空！");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(s);
        int code = jsonObject.getIntValue("code");
        if(0 == code){
            log.error("新享库获取美食商品失败，失败原因：{}",jsonObject.getString("msg"));
            return null;
        }
        return jsonObject;
    }

    /**
     * 享库获取 商品详情
     * @return
     */
    public static JSONObject getProductInfo(String url,String appId,String appSecret,String goodsId,String sourceType){
        long timeMillis = System.currentTimeMillis();
        //查询详情，详情接口参数
        Map<String, Object> detail = new HashMap<>();
        detail.put("appid", appId);
        detail.put("appsecret", appSecret);
        detail.put("goods_id", goodsId);
        detail.put("sourceType", sourceType);
        detail.put("timestamp", timeMillis);
        String sign = "appid=" + appId + "&appsecret=" + appSecret + "&timestamp=" + timeMillis;
        sign = DigestUtils.md5Hex(sign.toUpperCase());
        detail.put("sign", sign);
        url = url + "?s=openapiGoodsDetail";
        String s = null;
        try {
            s = HttpUtil.createPost(url).body(JSONObject.toJSONString(detail)).setReadTimeout(10000).execute().body();
        } catch (Exception e) {
            log.error("新享库获取商品详情出现异常，商品id：{}，异常信息：{}", goodsId, e.getMessage());
        }
        if(ObjectUtil.isNull(s)){
            log.error("新享库获取商品，返回空！");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(s);
        int code = jsonObject.getIntValue("code");
        if(0 == code){
            log.error("新享库获取美食商品失败商品id：{}，失败原因：{}",goodsId,jsonObject.getString("msg"));
            return null;
        }
        return jsonObject;

    }

    /**
     * 享库订单下单
     * @return
     */
    public static JSONObject createXkOrder(String url,String appId,String appSecret,String goodsId,String sourceType,String mobile,Long count){
        //创建享库订单
        long timeMillis = System.currentTimeMillis();
        //请求参数
        Map<String, Object> map = new HashMap<>();
        map.put("appid", appId);
        map.put("appsecret", appSecret);
        map.put("goods_id", goodsId);
        map.put("user_name", "匿名");
        map.put("phone", mobile);
        map.put("goods_num", count);
        map.put("sourceType", sourceType);
        map.put("timestamp", timeMillis);
        String sign = "appid=" + appId + "&appsecret=" + appSecret + "&timestamp=" + timeMillis;
        sign = DigestUtils.md5Hex(sign.toUpperCase());
        map.put("sign", sign);
        url = url + "?s=openapiCreateOrder";
        String s = HttpUtil.createPost(url).body(JSONObject.toJSONString(map)).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (jsonObject.getIntValue("code") == 0) {
            throw new ServiceException(jsonObject.getString("msg"));
        }

        return jsonObject;

    }

    /**
     * 享库订单支付
     * @param
     */
    public static JSONObject payXkOrder(String url,String appId,String appSecret,String sourceType,String exOrderNumber){
        //享库支付订单
        Map<String, Object> map = new HashMap<>();
        long timeMillis = System.currentTimeMillis();
        map.put("appid", appId);
        map.put("appsecret", appSecret);
        map.put("order_no", exOrderNumber);
        map.put("sourceType", sourceType);
        map.put("timestamp", timeMillis);
        String sign = "appid=" + appId + "&appsecret=" + appSecret + "&timestamp=" + timeMillis;
        sign = DigestUtils.md5Hex(sign.toUpperCase());
        map.put("sign", sign);

        url = url + "?s=openapiOrderPay";
        String s = HttpUtil.createPost(url).body(JSONObject.toJSONString(map)).execute().body();

        return JSONObject.parseObject(s);

    }

    /**
     * 享库查订单券码
     */
    public static JSONObject queryOrderCode(String url,String appId,String appSecret,String sourceType,String exOrderNumber){
        //查询订单券码信息
        Map<String, Object> m = new HashMap<>();
        long timeMillis = System.currentTimeMillis();
        m.put("appid", appId);
        m.put("appsecret", appSecret);
        m.put("order_no", exOrderNumber);
        m.put("sourceType", sourceType);
        m.put("timestamp", timeMillis);
        String sign = "appid=" + appId + "&appsecret=" + appSecret + "&timestamp=" + timeMillis;
        sign = DigestUtils.md5Hex(sign.toUpperCase());
        m.put("sign", sign);
        url = url + "?s=openapiVercodeDetail";

        String s = HttpUtil.createPost(url).body(JSONObject.toJSONString(m)).execute().body();
        if (ObjectUtil.isEmpty(s)){
            throw new ServiceException("享库订单查询核销码失败，返回结果：" + s);
        }

        return JSONObject.parseObject(s);
    }

    /**
     * 享库查订单状态
     */
    public static JSONObject queryOrderState(String url,String appId,String appSecret,String sourceType,String exOrderNumber){
        long timeMillis = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put("appid", appId);
        map.put("appsecret", appSecret);
        map.put("order_no", exOrderNumber);
        map.put("sourceType", sourceType);
        map.put("timestamp", timeMillis);
        String sign = "appid=" + appId + "&appsecret=" + appSecret + "&timestamp=" + timeMillis;
        System.out.println(sign.toUpperCase());
        sign = DigestUtils.md5Hex(sign.toUpperCase());
        map.put("sign", sign);

        url = url + "?s=openapiOrderDetail";
        String s = HttpUtil.createPost(url).body(JSONObject.toJSONString(map)).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(s);

        return jsonObject;

    }

    /**
     * * 享库退款
     * @param
     */
    public static JSONObject refundOrder(String url,String appId,String appSecret,String sourceType,String exOrderNumber,String orderCode,String refundReason){
        //请求参数
        long timeMillis = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put("appid", appId);
        map.put("appsecret", appSecret);
        map.put("order_no", exOrderNumber);
        map.put("code", orderCode);
        map.put("problem_desc", refundReason);
        map.put("sourceType", sourceType);
        map.put("timestamp", timeMillis);
        String sign = "appid=" + appId + "&appsecret=" + appSecret + "&timestamp=" + timeMillis;
        sign = DigestUtils.md5Hex(sign.toUpperCase());
        map.put("sign", sign);
        url = url + "?s=openapiOrderCodeRefund";
        String s = HttpUtil.createPost(url).body(JSONObject.toJSONString(map)).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (jsonObject.getIntValue("code") == 0) {
            throw new ServiceException(jsonObject.getString("msg"));
        }
        return jsonObject;

    }



    public static void main(String[] args) {
        String appid="7beedefbd76b8909992408fed373476e";
        String url="https://www.huibendi.com/web/index.php";
        String appSecret="55152143-BE96-410B-B010-8AEAA6DF5D78";
        String sourceType="yunzhigu";
        //System.out.println(getProductList(1, 10, "https://www.huibendi.com/web/index.php", "7beedefbd76b8909992408fed373476e", "55152143-BE96-410B-B010-8AEAA6DF5D78", "yunzhigu"));
        System.out.println(getProductInfo("https://www.huibendi.com/web/index.php",appid,appSecret,"572623",sourceType));
    }






}
