package com.ruoyi.zlyyh.utils.sdk;

import cfca.sm2.signature.SM2PrivateKey;
import cfca.sm2rsa.common.Mechanism;
import cfca.sm2rsa.common.PKIException;
import cfca.util.CertUtil;
import cfca.util.EnvelopeUtil;
import cfca.util.KeyUtil;
import cfca.util.SignatureUtil2;
import cfca.util.cipher.lib.JCrypto;
import cfca.util.cipher.lib.Session;
import cfca.x509.certificate.X509Cert;
import cfca.x509.certificate.X509CertHelper;
import cn.hutool.core.codec.Base64;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.common.core.utils.JsonUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.reflect.ReflectUtils;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.param.MsOrderVo;
import com.ruoyi.zlyyh.properties.utils.YsfDistributionPropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MsyhUtils {
    private static Session session;
    private static final String Order_prefix = "1086520672054111156214011101082";
    static {
        try {
            JCrypto.getInstance().initialize(JCrypto.JSOFT_LIB, null);
            session = JCrypto.getInstance().openSession(JCrypto.JSOFT_LIB);
        } catch (PKIException e) {
            e.printStackTrace();
        }
    }


    /**
     * 民生银行下单
     * @param collectiveNumber
     * @param orders
     * @param amount
     * @param notifyUrl
     * @param merchantNo
     * @param merchantKey
     * @param certName
     * @param merchantName
     * @param publicCertPath
     * @param orderTime
     * @param redirectUrl
     * @param platformId
     * @param contractId
     * @return
     */
    public static String pay(String collectiveNumber, List<Order> orders, String amount, String notifyUrl, String merchantNo, String merchantKey, String certName, String merchantName,String publicCertPath ,Date orderTime,String redirectUrl,String platformId,String contractId) {
        Map<String, String> contentData = new HashMap<>();
        contentData.put("platformId",platformId);
        contentData.put("merchantNo",merchantNo);
        contentData.put("productId","30010013");
        contentData.put("contractId",contractId);
        contentData.put("merchantName",merchantName);
        contentData.put("selectTradeType","CARD");
        contentData.put("isShowPayPage","0");
        contentData.put("amount",amount);
        //在此进行商户订单号补全50位
        String merchantSeq = Order_prefix+collectiveNumber;
        contentData.put("merchantSeq",merchantSeq);
        //yyyyMMdd
        contentData.put("transDate",DateFormatUtils.format(orderTime, "yyyyMMdd"));
        //yyyyMMddHHmmssSSS
        contentData.put("transTime",DateFormatUtils.format(orderTime, "yyyyMMddHHmmssSSS"));
        contentData.put("notifyUrl",notifyUrl);
        contentData.put("redirectUrl",redirectUrl);
        contentData.put("payType","01");
        contentData.put("recNum",Integer.valueOf(orders.size()).toString());
        //此处拼接小订单列表
        List<MsOrderVo> msOrderVos = new ArrayList<>();

        for (Order order : orders) {
            MsOrderVo msOrderVo = new MsOrderVo();
            msOrderVo.setProdAmt(order.getWantAmount().toString());
            msOrderVo.setProdCount(order.getCount().toString());
            msOrderVo.setProdId(order.getProductId().toString());
            msOrderVo.setProdName(order.getProductName());
            String inateMeroChannelSerialNo = Order_prefix + order.getNumber().toString();
            msOrderVo.setInateMeroChannelSerialNo(inateMeroChannelSerialNo);
            msOrderVos.add(msOrderVo);
        }
        contentData.put("prodList",JSONObject.toJSONString(msOrderVos));
        String context = JSON.toJSONString(contentData);
        //签名 私钥sm2证书签名
        String sign = getSign(context, certName, merchantKey);
        //拼装签名
        String signContext = sign(sign, context);
        //加密 公钥证书加密
        //加密完成返回加密字符串
        return encrypt(signContext, publicCertPath);

    }

    /**
     * * 民生银行支付结果查询
     * @param
     */
    public static String queryMsPay(String collectiveNumber, String merchantNo, String merchantKey, String certName,String platformId,String publicCertPath,String queryUrl) {
        Map<String, String> data = new HashMap<>();
        // 接入商户号
        data.put("platformId", platformId);
        // 商户号
        data.put("merchantNo", merchantNo);
        // 订单号
        data.put("mchSeqNo", Order_prefix+collectiveNumber);
        // 查询流水号
        data.put("merchantSeq", Order_prefix+IdUtil.getSnowflakeNextIdStr());
        // 订单号
        data.put("querySeq", Order_prefix+collectiveNumber);
        String context = JSON.toJSONString(data);
        //签名 私钥sm2证书签名
        String sign = getSign(context, certName, merchantKey);
        //拼装签名
        String signContext = sign(sign, context);
        //加密 公钥证书加密
        //加密完成返回加密字符串
        String encrypt = encrypt(signContext, publicCertPath);
        HashMap<String, String> business = new HashMap<>();
        business.put("businessContext",encrypt);
        JSONObject businessJson = JSONObject.parseObject(String.valueOf(business));
        String result = HttpUtil.createPost(queryUrl).addHeaders(business).body(JsonUtils.toJsonString(businessJson)).execute().body();
        if (ObjectUtil.isNotEmpty(result)){
            JSONObject jsonObject = JSONObject.parseObject(result);
            String businessContext = jsonObject.getString("businessContext");
            //解密后传输
            return dncrypt(businessContext, certName, merchantKey);
        }
        return null;
    }

    /**
     * 民生银行退款
     * @param
     */
    public static String msRefund(String platformId,String merchantNo,String collectiveNumber,String thNumber,String orderAmount,Object obj,String certName,String merchantKey,String publicCertPath,String cancelUrl){
        Map<String, String> data = new HashMap<>();
        // 接入商户号
        data.put("platformId", platformId);
        // 商户号
        data.put("merchantNo", merchantNo);
        // 商户订单号
        data.put("merchantSeq", Order_prefix+collectiveNumber);
        // 退款订单号
        data.put("mchSeqNo", Order_prefix+thNumber);
        // 退款金额
        data.put("orderAmount",orderAmount);

        //拼装xml
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>(); //默认
        dataMap.put("recNum", "1");
        Map<String, Object> prodKindOjb1 = new LinkedHashMap<String, Object>();
        //小订单拼接50位数
        prodKindOjb1.put("orgInateMeroChannelSerialNo", Order_prefix+ReflectUtils.invokeGetter(obj, "number"));
        prodKindOjb1.put("prodAmt", ReflectUtils.invokeGetter(obj, "wantAmount"));
        prodKindOjb1.put("prodId", ReflectUtils.invokeGetter(obj, "productId"));
        prodKindOjb1.put("prodName", ReflectUtils.invokeGetter(obj, "productName"));
        prodKindOjb1.put("prodCount", ReflectUtils.invokeGetter(obj, "count"));

        dataMap.put("prodList", prodKindOjb1);
        byte[] bytes = callMapToXML(dataMap);
        String string = Base64Utils.encodeToString(bytes);
        data.put("inForm",string);
        String context = JSON.toJSONString(data);
        //签名 私钥sm2证书签名
        String sign = getSign(context, certName, merchantKey);
        //拼装签名
        String signContext = sign(sign, context);
        //加密 公钥证书加密
        //加密完成返回加密字符串
        String encrypt = encrypt(signContext, publicCertPath);
        HashMap<String, String> business = new HashMap<>();
        business.put("businessContext",encrypt);
        JSONObject businessJson = JSONObject.parseObject(String.valueOf(business));
        String result = HttpUtil.createPost(cancelUrl).addHeaders(business).body(JsonUtils.toJsonString(businessJson)).execute().body();
        if (ObjectUtil.isNotEmpty(result)){
            JSONObject jsonObject = JSONObject.parseObject(result);
            String businessContext = jsonObject.getString("businessContext");
            //解密后传输
            return dncrypt(businessContext, certName, merchantKey);
        }
        return null;


    }




    public static void main(String[] args) {
        String context = "{\"amount\":\"2\"," + "\"defaultTradeType\":\"API_WXQRCODE\","
            + "\"isConfirm\":\"0\"," + "\"isShowSuccess\":\"0\"," + "\"merchantName\":\"乐收银测试\","
            + "\"merchantNum\":\"M01002016070000000789\"," + "\"merchantSeq\":\"10086201607052312\","
            + "\"notifyUrl\":\"http://111.205.207.103/merchantdemo/noticeServlet\"," + "\"orderInfo\":\"\","
            + "\"platformId\":\"cust0001\"," + "\"printFlag\":\"0\"," + "\"remark\":\"\","
            + "\"selectTradeType\":\"API_WXQRCODE\"," + "\"transDate\":\"20160627\","
            + "\"transTime\":\"201606270900000\"}";
        String sign = getSign(context, "D:\\云闪付新商城平台\\yzg-discounts\\ruoyi-common\\ruoyi-common-zlyyh\\src\\main\\resources\\cert\\cust0001.sm2", "123abc");
        System.out.println("--------------------------------------");
        System.out.println("签名：");
        System.out.println(sign);

        String signContext = sign(sign, context);
        System.out.println("--------------------------------------");
        System.out.println("加密前：");
        System.out.println(signContext);

        String encryptContext = encrypt(signContext, "D:\\云闪付新商城平台\\yzg-discounts\\ruoyi-common\\ruoyi-common-zlyyh\\src\\main\\resources\\cert\\cust0001.cer");
        System.out.println("--------------------------------------");
        System.out.println("加密后：");
        System.out.println(encryptContext);

        String dncryptContext = dncrypt(encryptContext, "D:\\云闪付新商城平台\\yzg-discounts\\ruoyi-common\\ruoyi-common-zlyyh\\src\\main\\resources\\cert\\cust0001.sm2", "123abc");
        System.out.println("--------------------------------------");
        System.out.println("解密后：");
        System.out.println(dncryptContext);

        String signChkResult = signCheck(dncryptContext, "D:\\云闪付新商城平台\\yzg-discounts\\ruoyi-common\\ruoyi-common-zlyyh\\src\\main\\resources\\cert\\cust0001.cer");
        System.out.println("--------------------------------------");
        System.out.println("验证签名结果：");
        System.out.println(signChkResult);

        String a = "10000000000000000000000000000001908221732534T00083";
        System.out.println(StringUtils.substring(a, 31, 50));
        System.out.println(JSONObject.parse(context));


        Map<String, Object> dataMap = new LinkedHashMap<String, Object>(); //默认
        dataMap.put("recNum", "1");
        dataMap.put("postscript", "我是postscript");


        Map<String, Object> prodKindOjb1 = new LinkedHashMap<String, Object>();
        prodKindOjb1.put("orgInateMeroChannelSerialNo", "UNPS-3300000100016169T10444596220000100016169T1111");
        prodKindOjb1.put("prodAmt", "1");
        prodKindOjb1.put("prodId", "PROD0001");
        prodKindOjb1.put("prodName", "商品名称");
        prodKindOjb1.put("prodCount", 1);
        prodKindOjb1.put("agtContractId", "");

//        List<Map<String, Object>> prodKindList1 = new ArrayList<Map<String, Object>>();
//        for(int i=1;i<=1;i++){
//            //第一层
//            Map<String, Object> prodKindOjb1 = new LinkedHashMap<String, Object>();
//            prodKindOjb1.put("idWltCloudProdKind", "类目ID");
//            prodKindOjb1.put("prodKindName", "类目名称");
//            prodKindOjb1.put("prodKindIdx", "类目序号");
//            Map<String, Object> prodKind1 = new LinkedHashMap<String, Object>();
////            prodKind1.put("123", prodKindOjb1);
////            prodKindList1.add(prodKind1);
//        }

        dataMap.put("prodList", prodKindOjb1);

        System.out.println(callMapToXML(dataMap));

        System.out.println(Base64Utils.encodeToString(callMapToXML(dataMap)));

        Map<String, List<String>> collect = new HashMap<>();
        List<String> result = new ArrayList<>();
        List<String> ab = new ArrayList<>();
        ab.add("1");
        ab.add("2");
        ab.add("3");
        List<String> ac = new ArrayList<>();
        ac.add("2");
        ac.add("3");
        ac.add("4");
        List<String> ad = new ArrayList<>();
        ad.add("5");
        ad.add("6");
        ad.add("7");
        List<String> ae = new ArrayList<>();
        ae.add("1d");
        ae.add("2d");
        ae.add("3d");
        collect.put("0",ab);
        collect.put("1",ac);
        collect.put("2",ad);
        collect.put("3",ae);
        System.out.println(collect);
        for (String s : collect.keySet()) {
//            System.out.println(collect.get(s));
            if (!s.equals("0") && !s.equals("2")){
                System.out.println(collect.get(s));
                    result.addAll(collect.get(s));
            }

        }
        System.out.println(result);



    }

    public static byte[] callMapToXML(Map map) {
        log.info("将Map转成Xml, Map：" + map.toString());
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><prodListBody>");
        mapToXMLTest2(map, sb);
        sb.append("</prodListBody>");
        log.info("将Map转成Xml, Xml：" + sb.toString());
        try {
            return sb.toString().getBytes("UTF-8");
        } catch (Exception e) {
        }
        return null;
    }
    private static void mapToXMLTest2(Map map, StringBuffer sb) {
        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value)
                value = "";
            if (value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) map.get(key);
                sb.append("<" + key + ">");
                for (int i = 0; i < list.size(); i++) {
                    HashMap hm = (HashMap) list.get(i);
                    mapToXMLTest2(hm, sb);
                }
                sb.append("</" + key + ">");

            } else {
                if (value instanceof HashMap) {
                    sb.append("<" + key + ">");
                    mapToXMLTest2((HashMap) value, sb);
                    sb.append("</" + key + ">");
                } else {
                    sb.append("<" + key + ">" + value + "</" + key + ">");
                }

            }

        }
    }



    /**
     * 签名
     *
     * @param sign
     * @param context
     * @return
     */
    public static String sign(String sign, String context) {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        Gson gson = builder.create();
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("sign", sign);
        paramMap.put("body", context);
        String signInfo = gson.toJson(paramMap); // 待加密字符串
        return signInfo;
    }

    /**
     * 加密
     *
     * @param signContext 需要加密的报文
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String encrypt(String signContext, String certAbsPath) {
        //String certAbsPath = Config.getProperty("merchantPublicKey");
        X509Cert cert = null;
        try {
            cert = X509CertHelper.parse(certAbsPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PKIException e) {
            e.printStackTrace();
        }
        X509Cert[] certs = {cert};
        byte[] encryptedData = null;
        try {
            encryptedData = EnvelopeUtil.envelopeMessage(signContext.getBytes("UTF8"), Mechanism.SM4_CBC, certs);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (PKIException e) {
            e.printStackTrace();
        }
        String encodeText = null;
        try {
            encodeText = new String(encryptedData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeText;
    }

    /**
     * 解密
     *
     * @param encryptContext 需要解密的报文
     * @return
     */
    public static String dncrypt(String encryptContext, String priKeyAbsPath, String priKeyPWD) {
        //String priKeyAbsPath = Config.getProperty("merchantPrivateKey");
        //String priKeyPWD = Config.getProperty("merchantPwd");
        String decodeText = null;
        try {
            PrivateKey priKey = KeyUtil.getPrivateKeyFromSM2(priKeyAbsPath, priKeyPWD);
            X509Cert cert = CertUtil.getCertFromSM2(priKeyAbsPath);
            byte[] sourceData = EnvelopeUtil.openEvelopedMessage(encryptContext.getBytes("UTF8"), priKey, cert, session);
            decodeText = new String(sourceData, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decodeText;
    }

    /**
     * 验证签名
     *
     * @param dncryptContext 需要验证签名的明文
     * @return
     */
    public static String signCheck(String dncryptContext, String certAbsPath) {
        //String certAbsPath = Config.getProperty("merchantPublicKey");
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        Map<String, Object> paraMap = gson.fromJson(dncryptContext, Map.class);
        String sign = paraMap.get("sign").toString();
        String body = paraMap.get("body").toString();
        boolean isSignOK = false;
        try {
            X509Cert cert = X509CertHelper.parse(certAbsPath);
            PublicKey pubKey = cert.getPublicKey();
            isSignOK = new SignatureUtil2().p1VerifyMessage(Mechanism.SM3_SM2, body.getBytes("UTF8"),
                sign.getBytes(), pubKey, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSignOK) {
            return "验签通过";
        } else {
            return "验签不通过";
        }
    }

    private static String getSign(String context, String priKeyAbsPath, String priKeyPWD) {
//        String priKeyAbsPath = Config.getProperty("merchantPrivateKey");
//        String priKeyPWD = Config.getProperty("merchantPwd");
        String sign = "";
        try {
            JCrypto.getInstance().initialize(JCrypto.JSOFT_LIB, null);
            Session session = JCrypto.getInstance().openSession(JCrypto.JSOFT_LIB);
            SM2PrivateKey priKey = KeyUtil.getPrivateKeyFromSM2(priKeyAbsPath, priKeyPWD);
            sign = new String(
                new SignatureUtil2().p1SignMessage(Mechanism.SM3_SM2, context.getBytes("UTF8"), priKey, session));
        } catch (PKIException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sign;
    }


}
