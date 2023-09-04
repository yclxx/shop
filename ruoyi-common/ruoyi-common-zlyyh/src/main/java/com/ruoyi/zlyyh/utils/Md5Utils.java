package com.ruoyi.zlyyh.utils;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * Md5加密方法
 *
 * @author ruoyi
 */
public class Md5Utils {
    private static final Logger log = LoggerFactory.getLogger(Md5Utils.class);

    /**
     * RSA编码
     */
    private static final String CHARSET = "UTF-8";

    private static byte[] md5(String s) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes("UTF-8"));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (Exception e) {
            log.error("MD5 Error...", e);
        }
        return null;
    }

    private static final String toHex(byte hash[]) {
        if (hash == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * 参数名ASCII码从小到大排序（字典序）；
     * 如果参数的值为空不能参与签名；
     * 参数名区分大小写；
     * 传送的sign参数不参与签名
     *
     * @param params 签名的参数
     * @param mchKey 密钥
     * @return sign全大写
     */
    public static String createMd5Sign(Map<String, String> params, String mchKey) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys); // 顺序
        // Collections.sort(keys, Collections.reverseOrder()); // 逆序
        StringBuilder sb = new StringBuilder();
        boolean firstElement = true;
        for (String key : keys) {
            if ("sign".equalsIgnoreCase(key) || StringUtils.isEmpty(params.get(key))) {
                continue;
            }
            if (firstElement) {
                firstElement = false;
            } else {
                sb.append("&");
            }
            sb.append(key).append("=").append(params.get(key));
        }
        if (StringUtils.isNotEmpty(mchKey)) {
            sb.append("&key=" + mchKey);
        }
        log.info("签名方法，拼接参数：" + sb.toString());
        return DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }




    public static JSONObject getAloneKeys(JSONObject json){
        ArrayList<String> aloneKeys = new ArrayList<>();
        for (String key : json.keySet()) {
            aloneKeys.add(key);
        }
        // 排序
        Md5Utils.wordSort(aloneKeys);
        // 整理排序后的json
        JSONObject newJson = new JSONObject(new LinkedHashMap<>());
        for (String key : aloneKeys) {
            newJson.put(key, json.get(key));
        }
        return newJson;
    }

    private static void wordSort(ArrayList<String> words) {
        for (int i = words.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (words.get(j).compareToIgnoreCase(words.get(j + 1)) > 0) {
                    String temp = words.get(j);
                    words.set(j, words.get(j + 1));
                    words.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Map 升序
     * @return
     */
    public static String sortMap(Map<String,Object> params){
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        boolean firstElement = true;
        for (String key : keys){
            if(firstElement){
                sb.append("{");
                firstElement = false;
            }else {
                sb.append(",");
            }
            sb.append("\"").append(key).append("\"").append(":").append("\"").append(params.get(key)).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }





    public static String hash(String s) {
        try {
            return new String(toHex(md5(s)).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            log.error("not supported charset...{}", e);
            return s;
        }
    }



    public static String encrypt(String md5) {
        return org.springframework.util.DigestUtils.md5DigestAsHex(md5.getBytes(Charset.forName("UTF-8")));
    }


    /**
     * 私钥加签
     *
     * @param content 原文
     * @param privateKey 私钥
     * @return 签名值
     */
    public static String signByPrivateKey(String content, String privateKey) {
        try {
            PrivateKey priKey = getPrivateKey(privateKey);
            Signature signature = Signature.getInstance("SHA256withRSA");// MD5withRSA
            signature.initSign(priKey);
            signature.update(content.getBytes(CHARSET));
            byte[] sign = signature.sign();
            String signStr = java.util.Base64.getEncoder().encodeToString(sign);
            return signStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取私钥
     * 将base64编码后的私钥字符串转成PrivateKey实例
     * @Description 将base64编码后的私钥字符串转成PrivateKey实例
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(privateKey.getBytes(CHARSET));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

}
