package com.ruoyi.zlyyh.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DESede;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.zlyyh.domain.vo.MemberVipBalanceVo;
import com.ruoyi.zlyyh.domain.vo.ProductInfoVo;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 云闪付美食套餐相关接口帮助类
 *
 * @author 25487
 */
@Slf4j
public class YsfFoodUtils {
    /**
     * 查询品牌列表
     *
     */
    public static String getBrandList(Integer pageSize, Integer pageNum, String appId, String privateKey,String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("pageNum",pageNum);
        data.put("pageSize",pageSize);
        String s = Md5Utils.sortMap(data);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", DateUtils.getTime());
        params.put("data",s);
        String sortMap = Md5Utils.sortMap(params);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,privateKey);
        params.put("sign",sign);
        String paramsJson = JSONObject.toJSONString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(url, paramsJson);
        } catch (Exception e) {
            log.error("查询品牌列表接口失败：", e);
            return null;
        }
        log.info("查询品牌列表,请求参数：{},返回结果：{}", paramsJson, result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if (null == resultJson) {
            return null;
        }
        //{"msg":"操作成功","code":200,"data":{"total":29,"data":[{"brandId":1,"brandName":"尊宝比萨","brandLogo":null},{"brandId":2,"brandName":"好利来","brandLogo":null},{"brandId":3,"brandName":"哈根达斯","brandLogo":null},{"brandId":4,"brandName":"GODIVA","brandLogo":null},{"brandId":5,"brandName":"ANGSI昂司蛋糕","brandLogo":null},{"brandId":6,"brandName":"诺心 LE CAKE","brandLogo":null},{"brandId":7,"brandName":"子情贝诺","brandLogo":null},{"brandId":8,"brandName":"华荣西饼屋","brandLogo":null},{"brandId":9,"brandName":"南宋胡记","brandLogo":null},{"brandId":10,"brandName":"酥礼记","brandLogo":null},{"brandId":11,"brandName":"KOI Thé","brandLogo":null},{"brandId":12,"brandName":"熊猫不走生日蛋糕","brandLogo":null},{"brandId":13,"brandName":"幸福西饼","brandLogo":null},{"brandId":14,"brandName":"欢牛蛋糕屋","brandLogo":null},{"brandId":15,"brandName":"津乐园","brandLogo":null},{"brandId":16,"brandName":"华莱士","brandLogo":null},{"brandId":17,"brandName":"汉堡王","brandLogo":null},{"brandId":18,"brandName":"麦卡珑双拼比萨","brandLogo":null},{"brandId":19,"brandName":"大卡司","brandLogo":null},{"brandId":20,"brandName":"爱维尔阳光蛋糕","brandLogo":null}]}}
        String dataResult = resultJson.getString("data");
        String code = resultJson.getString("code");
        if ("200".equals(code)) {
            return dataResult;
        } else {
            log.error("查询品牌列表接口失败：{}", result);
        }
        return null;
    }
    /**
     * 查询品牌关联门店及商品
     *
     */
    public static String getShopAndCommodityByBrandId(Integer pageSize, Integer pageNum, String appId,Long brandId ,String privateKey,String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("pageNum",pageNum);
        data.put("pageSize",pageSize);
        data.put("brandId",brandId);
        String s = Md5Utils.sortMap(data);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", DateUtils.getTime());
        params.put("data",s);
        String sortMap = Md5Utils.sortMap(params);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,privateKey);
        params.put("sign",sign);
        String paramsJson = JSONObject.toJSONString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(url, paramsJson);
        } catch (Exception e) {
            log.error("查询商品门店列表接口失败：", e);
            return null;
        }
        log.info("查询商品门店列表,请求参数：{},返回结果：{}", paramsJson, result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if (null == resultJson) {
            return null;
        }
        //{"msg":"操作成功","code":200,"data":{"total":1341,"data":{"shopList":[{"brandShopId":3841,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3842,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3843,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3844,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3845,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3846,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3847,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3848,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3849,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3850,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3851,"commodityVoList":[]},{"brandShopId":3852,"commodityVoList":[{"commodityId":353709},{"commodityId":353709},{"commodityId":353717},{"commodityId":353717},{"commodityId":465361},{"commodityId":465361},{"commodityId":465362},{"commodityId":465362},{"commodityId":465363},{"commodityId":465363},{"commodityId":465364},{"commodityId":465364},{"commodityId":465365},{"commodityId":465365},{"commodityId":465366},{"commodityId":465366},{"commodityId":465367},{"commodityId":465367},{"commodityId":465368},{"commodityId":465368},{"commodityId":465369},{"commodityId":465369},{"commodityId":392867},{"commodityId":392867}]},{"brandShopId":3853,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3854,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3855,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3856,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3857,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3858,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3859,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]},{"brandShopId":3860,"commodityVoList":[{"commodityId":353709},{"commodityId":353717},{"commodityId":465361},{"commodityId":465362},{"commodityId":465363},{"commodityId":465364},{"commodityId":465365},{"commodityId":465366},{"commodityId":465367},{"commodityId":465368},{"commodityId":465369},{"commodityId":392867}]}],"brandId":17}}}       String code = dict.getStr("code");
        String dataResult = resultJson.getString("data");
        String code = resultJson.getString("code");
        if ("200".equals(code)) {
            return dataResult;
        } else {
            log.error("查询品牌列表接口失败：{}", result);
        }
        return null;
    }

    /**
     * 查询门店详情
     *
     */
    public static String getShopInfo(String appId,Long brandShopId ,String privateKey,String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("brandShopId",brandShopId);
        String s = Md5Utils.sortMap(data);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", DateUtils.getTime());
        params.put("data",s);
        String sortMap = Md5Utils.sortMap(params);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,privateKey);
        params.put("sign",sign);
        String paramsJson = JSONObject.toJSONString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(url, paramsJson);
        } catch (Exception e) {
            log.error("查询商品门店详情接口失败：", e);
            return null;
        }
        log.info("查询门店详情,请求参数：{},返回结果：{}", paramsJson, result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if (null == resultJson) {
            return null;
        }
        //{"msg":"操作成功","code":200,"data":{"id":3841,"shopName":"汉堡王(西铁营万达广场店)","shopTel":"4008988788","businessHours":null,"address":"北京市玉林西路西铁营万达广场6层6F6008A","formattedAddress":"北京市丰台区西铁营","country":"中国","province":"北京市","city":"北京市","citycode":"010","district":"丰台区","adcode":"110106","longitude":116.356232,"latitude":39.860900,"state":"0"}}        String code = dict.getStr("code");
        String dataResult = resultJson.getString("data");
        String code = resultJson.getString("code");
        if ("200".equals(code)) {
            return dataResult;
        } else {
            log.error("查询门店详情接口失败：{}", result);
        }
        return null;
    }

    /**
     * 查询商品详情
     *
     */
    public static String getProductInfo(String appId,String commodityId ,String privateKey,String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("commodityId",commodityId);
        String s = Md5Utils.sortMap(data);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", DateUtils.getTime());
        params.put("data",s);
        String sortMap = Md5Utils.sortMap(params);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,privateKey);
        params.put("sign",sign);
        String paramsJson = JSONObject.toJSONString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(url, paramsJson);
        } catch (Exception e) {
            log.error("查询商品详情接口失败：", e);
            return null;
        }
        log.info("查询商品详情,请求参数：{},返回结果：{}", paramsJson, result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if (null == resultJson) {
            return null;
        }
        //{"msg":"操作成功","code":200,"data":{"id":3841,"shopName":"汉堡王(西铁营万达广场店)","shopTel":"4008988788","businessHours":null,"address":"北京市玉林西路西铁营万达广场6层6F6008A","formattedAddress":"北京市丰台区西铁营","country":"中国","province":"北京市","city":"北京市","citycode":"010","district":"丰台区","adcode":"110106","longitude":116.356232,"latitude":39.860900,"state":"0"}}        String code = dict.getStr("code");
        String dataResult = resultJson.getString("data");
        String code = resultJson.getString("code");
        if ("200".equals(code)) {
            return dataResult;
        } else {
            log.error("查询商品详情接口失败：{}", result);
        }
        return null;
    }

    /**
     * * 创建订单
     * *
     */
    public static String createOrder(String appId,String commodityId,String productId,String mobile,String privateKey,String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("commodityId",commodityId);
        data.put("count",1);
        data.put("consigneeName","匿名");
        data.put("consigneeMobile",mobile);
        data.put("productId",productId);
        String s = Md5Utils.sortMap(data);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", DateUtils.getTime());
        params.put("data",s);
        String sortMap = Md5Utils.sortMap(params);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,privateKey);
        params.put("sign",sign);
        String paramsJson = JSONObject.toJSONString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(url, paramsJson);
        } catch (Exception e) {
            log.error("创建美食订单接口失败：", e);
            return null;
        }
        log.info("创建美食订单接口,请求参数：{},返回结果：{}", paramsJson, result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if (null == resultJson) {
            return null;
        }
        String dataResult = resultJson.getString("data");
        String code = resultJson.getString("code");
        if ("200".equals(code)) {
            return dataResult;
        } else {
            log.error("创建美食订单接口失败：{}", result);
            throw new ServiceException(resultJson.getString("msg"));
        }

    }


    /**
     * 支付订单
     * *
     */
    public static String payOrder(String appId,String number,String privateKey,String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("number",number);
        String s = Md5Utils.sortMap(data);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", DateUtils.getTime());
        params.put("data",s);
        String sortMap = Md5Utils.sortMap(params);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,privateKey);
        params.put("sign",sign);
        String paramsJson = JSONObject.toJSONString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(url, paramsJson);
        } catch (Exception e) {
            log.error("支付美食订单接口失败：", e);
            return null;
        }
        log.info("支付美食订单接口,请求参数：{},返回结果：{}", paramsJson, result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if (null == resultJson) {
            return null;
        }
        String msg = resultJson.getString("msg");
        String code = resultJson.getString("code");
        if ("200".equals(code)) {
            return msg;
        } else {
            log.error("支付美食订单接口失败：{}", result);
        }
        return null;
    }


    /**
     * 查询订单
     * * @param args
     */
    public static String queryOrder(String appId,String number,String privateKey,String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("number",number);
        String s = Md5Utils.sortMap(data);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", DateUtils.getTime());
        params.put("data",s);
        String sortMap = Md5Utils.sortMap(params);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,privateKey);
        params.put("sign",sign);
        String paramsJson = JSONObject.toJSONString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(url, paramsJson);
        } catch (Exception e) {
            log.error("查询美食订单接口失败：", e);
            return null;
        }
        log.info("查询订单接口,请求参数：{},返回结果：{}", paramsJson, result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if (null == resultJson) {
            return null;
        }
        String dataResult = resultJson.getString("data");
        String code = resultJson.getString("code");
        if ("200".equals(code)) {
            return dataResult;
        } else {
            log.error("查询订单接口失败：{}", result);
        }
        return null;
    }


    /**
     * 退款
     * * @param args
     */
    public static String cancelOrder(String appId,String number,String privateKey,String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("number",number);
        String s = Md5Utils.sortMap(data);
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", DateUtils.getTime());
        params.put("data",s);
        String sortMap = Md5Utils.sortMap(params);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,privateKey);
        params.put("sign",sign);
        String paramsJson = JSONObject.toJSONString(params);
        // 发送https 请求
        String result;
        try {
            result = HttpUtil.post(url, paramsJson);
        } catch (Exception e) {
            log.error("退款订单接口失败：", e);
            return null;
        }
        log.info("退款订单接口,请求参数：{},返回结果：{}", paramsJson, result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if (null == resultJson) {
            return null;
        }
        String msg = resultJson.getString("msg");
        String code = resultJson.getString("code");
        if ("200".equals(code)) {
            return msg;
        } else {
            log.error("退款订单接口失败：{}", result);
        }
        return null;
    }



    public static void main(String[] args) {
        String url = "https://yinlianmalladmin.yzgnet.com/prod-api/yinlianmall/api/brandList";
        String key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRRWWInHE/3Qsw0+35WT8eSR6XqeQVzdEz2Dsf6VdJS8c2eEaX+cCh7vjIFty7ENxkd7aKc4+HWSQO2dfy83p/cxsNXpJ7bfDB4SZ5S6Rr6irHCSZQymRuvGaBcjpnVX+qB3adp0UpGtG3z+AkTXnnnxKcFrYu3XF8GJb5Khbf5AgMBAAECgYACmRozufOpAu/Mm/D72Y80M7/FjEHqcodLAdRodF4kfQd3TpmIith0HRbL0YXP7+f3LKsI+i6Tuik1Q3D1qGlN5K2Xs+wPAh1br7nGVFPCJ39gO7081bY63fCDRt5rYcXyvNLXrr4hYp8HgKIO4dZCoObDpYipUz84f9uKuoXdwQJBAOG6UVIGgwtZrz1hQa9jgSF9OBTBtW5fOTJ1BLXFh88L/dy2OsdzOShR0AzfuGR8jRe01h2s3bs1S4ja5OICldUCQQCoNWRN6/MwYzfYdp8RqZ55RNakSGlCNGda4saaz7RSvfTyZ4jTBbtSBykgud9snuwbkex9yaGEgTkoi2M0pPeVAkEAsBpEwUKVT/CbF77dmPB/WNoxO3hYjJA7tlK25v0BVBWd62g7+Ui6aetR7glH+RV2me0aMrKfliMhF9b2RCESNQJAEExMEbjA8XlLme+0bfOvZTSkT3qsqDuHoCjE8Y8ae8HoD+y0Ny4g/kuvUnpwCYhEfE9hSLbWrY4PybvnutwZGQJAJ8mlk7C1i872Gx4zu+S89f/kS2WZzMHPJ7KHFHntzA2UxgXz6NxhX4UIALZSjh4nFvidhoZCPxpJbOC8APzX3w==";
        int pageSize = 20;
        int pageNum = 1;
        String brandList = getBrandList(pageSize, pageNum, "123456", key, url);
        JSONObject jsonObject = JSONObject.parseObject(brandList);
        Integer total = jsonObject.getInteger("total");
        JSONArray dataB = jsonObject.getJSONArray("data");
        System.out.println(total);

        for (int i = 0; i < dataB.size(); i++) {
            JSONObject brand = dataB.getJSONObject(i);
            System.out.println(brand);
            String list = getShopAndCommodityByBrandId(pageSize, pageNum, "123456", brand.getLong("brandId"), key, "https://yinlianmalladmin.yzgnet.com/prod-api/yinlianmall/api/shopAndCommodityByBrandId");
            JSONObject jsonObjectProduct = JSONObject.parseObject(list);
            Integer totalProduct = jsonObjectProduct.getInteger("total");
            JSONObject data1 = jsonObjectProduct.getJSONObject("data");
            if (ObjectUtil.isEmpty(data1)) {
                break;
            }
            JSONArray shopList = data1.getJSONArray("shopList");
            System.out.println(totalProduct);
            System.out.println("这是辣个"+shopList);


        }


        HashMap<String, Object> data = new HashMap<>();
        data.put("pageNum",pageNum);
        data.put("pageSize",pageSize);
        String s = Md5Utils.sortMap(data);
        HashMap<String, Object> testMap = new HashMap<>();
        testMap.put("data",s);
        testMap.put("timestamp", DateUtils.getTime());
        testMap.put("appId","123456");
        String sortMap = Md5Utils.sortMap(testMap);
        String sign;
        sign = Md5Utils.signByPrivateKey(sortMap,"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRRWWInHE/3Qsw0+35WT8eSR6XqeQVzdEz2Dsf6VdJS8c2eEaX+cCh7vjIFty7ENxkd7aKc4+HWSQO2dfy83p/cxsNXpJ7bfDB4SZ5S6Rr6irHCSZQymRuvGaBcjpnVX+qB3adp0UpGtG3z+AkTXnnnxKcFrYu3XF8GJb5Khbf5AgMBAAECgYACmRozufOpAu/Mm/D72Y80M7/FjEHqcodLAdRodF4kfQd3TpmIith0HRbL0YXP7+f3LKsI+i6Tuik1Q3D1qGlN5K2Xs+wPAh1br7nGVFPCJ39gO7081bY63fCDRt5rYcXyvNLXrr4hYp8HgKIO4dZCoObDpYipUz84f9uKuoXdwQJBAOG6UVIGgwtZrz1hQa9jgSF9OBTBtW5fOTJ1BLXFh88L/dy2OsdzOShR0AzfuGR8jRe01h2s3bs1S4ja5OICldUCQQCoNWRN6/MwYzfYdp8RqZ55RNakSGlCNGda4saaz7RSvfTyZ4jTBbtSBykgud9snuwbkex9yaGEgTkoi2M0pPeVAkEAsBpEwUKVT/CbF77dmPB/WNoxO3hYjJA7tlK25v0BVBWd62g7+Ui6aetR7glH+RV2me0aMrKfliMhF9b2RCESNQJAEExMEbjA8XlLme+0bfOvZTSkT3qsqDuHoCjE8Y8ae8HoD+y0Ny4g/kuvUnpwCYhEfE9hSLbWrY4PybvnutwZGQJAJ8mlk7C1i872Gx4zu+S89f/kS2WZzMHPJ7KHFHntzA2UxgXz6NxhX4UIALZSjh4nFvidhoZCPxpJbOC8APzX3w==");
        testMap.put("sign",sign);
        String s1 = JSONObject.toJSONString(testMap);

        System.out.println(s1);
        String post = HttpUtil.post(url, s1);


//        String body = HttpUtil.createPost(url).body(s1).execute().body();
        System.out.println(post);

        url= "https://yinlianmalladmin.yzgnet.com/prod-api/yinlianmall/api/shopAndCommodityByBrandId";
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("pageNum",pageNum);
        objectObjectHashMap.put("pageSize",pageSize);
        objectObjectHashMap.put("brandId","17");
        String s2 = Md5Utils.sortMap(objectObjectHashMap);
        HashMap<String, Object> objectObjectHashMap1 = new HashMap<>();
        objectObjectHashMap1.put("data",s2);
        objectObjectHashMap1.put("appId","123456");
        objectObjectHashMap1.put("timestamp", DateUtils.getTime());
        String sortMap1 = Md5Utils.sortMap(objectObjectHashMap1);
        String sign1;
        sign1 = Md5Utils.signByPrivateKey(sortMap1,"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRRWWInHE/3Qsw0+35WT8eSR6XqeQVzdEz2Dsf6VdJS8c2eEaX+cCh7vjIFty7ENxkd7aKc4+HWSQO2dfy83p/cxsNXpJ7bfDB4SZ5S6Rr6irHCSZQymRuvGaBcjpnVX+qB3adp0UpGtG3z+AkTXnnnxKcFrYu3XF8GJb5Khbf5AgMBAAECgYACmRozufOpAu/Mm/D72Y80M7/FjEHqcodLAdRodF4kfQd3TpmIith0HRbL0YXP7+f3LKsI+i6Tuik1Q3D1qGlN5K2Xs+wPAh1br7nGVFPCJ39gO7081bY63fCDRt5rYcXyvNLXrr4hYp8HgKIO4dZCoObDpYipUz84f9uKuoXdwQJBAOG6UVIGgwtZrz1hQa9jgSF9OBTBtW5fOTJ1BLXFh88L/dy2OsdzOShR0AzfuGR8jRe01h2s3bs1S4ja5OICldUCQQCoNWRN6/MwYzfYdp8RqZ55RNakSGlCNGda4saaz7RSvfTyZ4jTBbtSBykgud9snuwbkex9yaGEgTkoi2M0pPeVAkEAsBpEwUKVT/CbF77dmPB/WNoxO3hYjJA7tlK25v0BVBWd62g7+Ui6aetR7glH+RV2me0aMrKfliMhF9b2RCESNQJAEExMEbjA8XlLme+0bfOvZTSkT3qsqDuHoCjE8Y8ae8HoD+y0Ny4g/kuvUnpwCYhEfE9hSLbWrY4PybvnutwZGQJAJ8mlk7C1i872Gx4zu+S89f/kS2WZzMHPJ7KHFHntzA2UxgXz6NxhX4UIALZSjh4nFvidhoZCPxpJbOC8APzX3w==");
        objectObjectHashMap1.put("sign",sign1);
        String s3 = JSONObject.toJSONString(objectObjectHashMap1);

        System.out.println(s3);
        String post1 = HttpUtil.post(url, s3);
        System.out.println(post1);

        url= "https://yinlianmalladmin.yzgnet.com/prod-api/yinlianmall/api/brandShopByShopId";
        HashMap<String, Object> objectObjectHashMap3 = new HashMap<>();
        objectObjectHashMap3.put("brandShopId","3841");
        String s4 = Md5Utils.sortMap(objectObjectHashMap3);
        HashMap<String, Object> objectObjectHashMap2 = new HashMap<>();
        objectObjectHashMap2.put("data",s4);
        objectObjectHashMap2.put("appId","123456");
        objectObjectHashMap2.put("timestamp", DateUtils.getTime());
        String sortMap2 = Md5Utils.sortMap(objectObjectHashMap2);
        String sign2;
        sign2 = Md5Utils.signByPrivateKey(sortMap2,"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRRWWInHE/3Qsw0+35WT8eSR6XqeQVzdEz2Dsf6VdJS8c2eEaX+cCh7vjIFty7ENxkd7aKc4+HWSQO2dfy83p/cxsNXpJ7bfDB4SZ5S6Rr6irHCSZQymRuvGaBcjpnVX+qB3adp0UpGtG3z+AkTXnnnxKcFrYu3XF8GJb5Khbf5AgMBAAECgYACmRozufOpAu/Mm/D72Y80M7/FjEHqcodLAdRodF4kfQd3TpmIith0HRbL0YXP7+f3LKsI+i6Tuik1Q3D1qGlN5K2Xs+wPAh1br7nGVFPCJ39gO7081bY63fCDRt5rYcXyvNLXrr4hYp8HgKIO4dZCoObDpYipUz84f9uKuoXdwQJBAOG6UVIGgwtZrz1hQa9jgSF9OBTBtW5fOTJ1BLXFh88L/dy2OsdzOShR0AzfuGR8jRe01h2s3bs1S4ja5OICldUCQQCoNWRN6/MwYzfYdp8RqZ55RNakSGlCNGda4saaz7RSvfTyZ4jTBbtSBykgud9snuwbkex9yaGEgTkoi2M0pPeVAkEAsBpEwUKVT/CbF77dmPB/WNoxO3hYjJA7tlK25v0BVBWd62g7+Ui6aetR7glH+RV2me0aMrKfliMhF9b2RCESNQJAEExMEbjA8XlLme+0bfOvZTSkT3qsqDuHoCjE8Y8ae8HoD+y0Ny4g/kuvUnpwCYhEfE9hSLbWrY4PybvnutwZGQJAJ8mlk7C1i872Gx4zu+S89f/kS2WZzMHPJ7KHFHntzA2UxgXz6NxhX4UIALZSjh4nFvidhoZCPxpJbOC8APzX3w==");
        objectObjectHashMap2.put("sign",sign2);
        String s5 = JSONObject.toJSONString(objectObjectHashMap2);

        System.out.println(s5);
        String post2 = HttpUtil.post(url, s5);
        System.out.println(post2);

        System.out.println(getProductInfo("123456", "228560", "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRRWWInHE/3Qsw0+35WT8eSR6XqeQVzdEz2Dsf6VdJS8c2eEaX+cCh7vjIFty7ENxkd7aKc4+HWSQO2dfy83p/cxsNXpJ7bfDB4SZ5S6Rr6irHCSZQymRuvGaBcjpnVX+qB3adp0UpGtG3z+AkTXnnnxKcFrYu3XF8GJb5Khbf5AgMBAAECgYACmRozufOpAu/Mm/D72Y80M7/FjEHqcodLAdRodF4kfQd3TpmIith0HRbL0YXP7+f3LKsI+i6Tuik1Q3D1qGlN5K2Xs+wPAh1br7nGVFPCJ39gO7081bY63fCDRt5rYcXyvNLXrr4hYp8HgKIO4dZCoObDpYipUz84f9uKuoXdwQJBAOG6UVIGgwtZrz1hQa9jgSF9OBTBtW5fOTJ1BLXFh88L/dy2OsdzOShR0AzfuGR8jRe01h2s3bs1S4ja5OICldUCQQCoNWRN6/MwYzfYdp8RqZ55RNakSGlCNGda4saaz7RSvfTyZ4jTBbtSBykgud9snuwbkex9yaGEgTkoi2M0pPeVAkEAsBpEwUKVT/CbF77dmPB/WNoxO3hYjJA7tlK25v0BVBWd62g7+Ui6aetR7glH+RV2me0aMrKfliMhF9b2RCESNQJAEExMEbjA8XlLme+0bfOvZTSkT3qsqDuHoCjE8Y8ae8HoD+y0Ny4g/kuvUnpwCYhEfE9hSLbWrY4PybvnutwZGQJAJ8mlk7C1i872Gx4zu+S89f/kS2WZzMHPJ7KHFHntzA2UxgXz6NxhX4UIALZSjh4nFvidhoZCPxpJbOC8APzX3w=="
            , "https://yinlianmalladmin.yzgnet.com/prod-api/yinlianmall/api/commodityByCommodityId"));

        System.out.println(queryOrder("123456", "y1661576001680969728", key, "https://yinlianmalladmin.yzgnet.com/prod-api/yinlianmall/api/orderInfoByNumber"));
    }
}
