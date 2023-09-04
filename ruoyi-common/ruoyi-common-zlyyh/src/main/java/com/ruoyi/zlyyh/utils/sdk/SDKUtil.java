/**
 * Licensed Property to China UnionPay Co., Ltd.
 * <p>
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 * All Rights Reserved.
 * <p>
 * <p>
 * Modification History:
 * =============================================================================
 * Author         Date          Description
 * ------------ ---------- ---------------------------------------------------
 * xshu       2014-05-28      MPI工具类
 * =============================================================================
 */
package com.ruoyi.zlyyh.utils.sdk;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

import static com.ruoyi.zlyyh.utils.sdk.SDKConstants.*;

/**
 * @ClassName SDKUtil
 * @Description acpsdk工具类
 * @date 2016-7-22 下午4:06:18
 * 声明：以下代码只是为了方便接入方测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障
 */
public class SDKUtil {

    /**
     * 通过传入的签名密钥进行签名并返回签名值<br>
     *
     * @param data     待签名数据Map键值对形式
     * @param encoding 编码
     * @param certPath 证书绝对路径
     * @param certPwd  证书密码
     * @return 签名值
     */
    public static boolean signByCertInfo(Map<String, String> data,
                                         String certPath, String certPwd, String encoding) {

        if (isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        if (isEmpty(certPath) || isEmpty(certPwd)) {
            LogUtil.writeErrorLog("CertPath or CertPwd is empty");
            return false;
        }
        String signMethod = data.get(param_signMethod);
        String version = data.get(SDKConstants.param_version);
        if (!VERSION_1_0_0.equals(version) && !VERSION_5_0_1.equals(version) && isEmpty(signMethod)) {
            LogUtil.writeErrorLog("signMethod must Not null");
            return false;
        }
        if (isEmpty(version)) {
            LogUtil.writeErrorLog("version must Not null");
            return false;
        }

        if (SIGNMETHOD_RSA.equals(signMethod) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
            if (VERSION_5_0_0.equals(version) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
                // 设置签名证书序列号
                data.put(SDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
                // 将Map信息转换成key1=value1&key2=value2的形式
                String stringData = coverMap2String(data);
                LogUtil.writeLog("待签名请求报文串:[" + stringData + "]");
                byte[] byteSign = null;
                String stringSign = null;
                try {
                    // 通过SHA1进行摘要并转16进制
                    byte[] signDigest = SecureUtil
                            .sha1X16(stringData, encoding);
                    byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
                            CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
                    stringSign = new String(byteSign);
                    // 设置签名域值
                    data.put(SDKConstants.param_signature, stringSign);
                    return true;
                } catch (Exception e) {
                    LogUtil.writeErrorLog("Sign Error", e);
                    return false;
                }
            } else if (VERSION_5_1_0.equals(version)) {
                // 设置签名证书序列号
                data.put(SDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
                // 将Map信息转换成key1=value1&key2=value2的形式
                String stringData = coverMap2String(data);
                LogUtil.writeLog("待签名请求报文串:[" + stringData + "]");
                byte[] byteSign = null;
                String stringSign = null;
                try {
                    // 通过SHA256进行摘要并转16进制
                    byte[] signDigest = SecureUtil
                            .sha256X16(stringData, encoding);
                    byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft256(
                            CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
                    stringSign = new String(byteSign);
                    // 设置签名域值
                    data.put(SDKConstants.param_signature, stringSign);
                    return true;
                } catch (Exception e) {
                    LogUtil.writeErrorLog("Sign Error", e);
                    return false;
                }
            }

        }
        return false;
    }

    /**
     * 验证签名
     *
     * @param resData  返回报文数据
     * @param encoding 编码格式
     * @return
     */
    public static boolean validate(Map<String, String> resData, String encoding, boolean showLog) {
        if (showLog) {
            LogUtil.writeLog("验签处理开始");
        }
        if (isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        String signMethod = resData.get(param_signMethod);
        String version = resData.get(SDKConstants.param_version);
        if (SIGNMETHOD_RSA.equals(signMethod) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
            // 获取返回报文的版本号
            if (VERSION_5_0_0.equals(version) || VERSION_1_0_0.equals(version) || VERSION_5_0_1.equals(version)) {
                String stringSign = resData.get(SDKConstants.param_signature);
                if (showLog) {
                    LogUtil.writeLog("签名原文：[" + stringSign + "]");
                }
                // 从返回报文中获取certId ，然后去证书静态Map中查询对应验签证书对象
                String certId = resData.get(SDKConstants.param_certId);
                if (showLog) {
                    LogUtil.writeLog("对返回报文串验签使用的验签公钥序列号：[" + certId + "]");
                }
                // 将Map信息转换成key1=value1&key2=value2的形式
                String stringData = coverMap2String(resData);
                if (showLog) {
                    LogUtil.writeLog("待验签返回报文串：[" + stringData + "]");
                }
                try {
                    // 验证签名需要用银联发给商户的公钥证书.
                    return SecureUtil.validateSignBySoft(CertUtil
                                    .getValidatePublicKey(certId), SecureUtil
                                    .base64Decode(stringSign.getBytes(encoding)),
                            SecureUtil.sha1X16(stringData, encoding));
                } catch (UnsupportedEncodingException e) {
                    LogUtil.writeErrorLog(e.getMessage(), e);
                } catch (Exception e) {
                    LogUtil.writeErrorLog(e.getMessage(), e);
                }
            } else if (VERSION_5_1_0.equals(version)) {
                // 1.从返回报文中获取公钥信息转换成公钥对象
                String strCert = resData.get(SDKConstants.param_signPubKeyCert);
//				LogUtil.writeLog("验签公钥证书：["+strCert+"]");
                X509Certificate x509Cert = CertUtil.genCertificateByStr(strCert);
                if (x509Cert == null) {
                    LogUtil.writeErrorLog("convert signPubKeyCert failed");
                    return false;
                }
                // 2.验证证书链
                if (!CertUtil.verifyCertificate(x509Cert)) {
                    LogUtil.writeErrorLog("验证公钥证书失败，证书信息：[" + strCert + "]");
                    return false;
                }

                // 3.验签
                String stringSign = resData.get(SDKConstants.param_signature);
                if (showLog) {
                    LogUtil.writeLog("签名原文：[" + stringSign + "]");
                }
                // 将Map信息转换成key1=value1&key2=value2的形式
                String stringData = coverMap2String(resData);
                if (showLog) {
                    LogUtil.writeLog("待验签返回报文串：[" + stringData + "]");
                }
                try {
                    // 验证签名需要用银联发给商户的公钥证书.
                    boolean result = SecureUtil.validateSignBySoft256(x509Cert
                            .getPublicKey(), SecureUtil.base64Decode(stringSign
                            .getBytes(encoding)), SecureUtil.sha256X16(
                            stringData, encoding));
                    if (showLog) {
                        LogUtil.writeLog("验证签名" + (result ? "成功" : "失败"));
                    }
                    return result;
                } catch (UnsupportedEncodingException e) {
                    LogUtil.writeErrorLog(e.getMessage(), e);
                } catch (Exception e) {
                    LogUtil.writeErrorLog(e.getMessage(), e);
                }
            }

        } else if (SIGNMETHOD_SHA256.equals(signMethod)) {
            // 1.进行SHA256验证
            String stringSign = resData.get(SDKConstants.param_signature);
            if(showLog){
                LogUtil.writeLog("签名原文：[" + stringSign + "]");
            }
            // 将Map信息转换成key1=value1&key2=value2的形式
            String stringData = coverMap2String(resData);
            if(showLog){
                LogUtil.writeLog("待验签返回报文串：[" + stringData + "]");
            }
            String strBeforeSha256 = stringData
                    + SDKConstants.AMPERSAND
                    + SecureUtil.sha256X16Str(SDKConfig.getSecureKey(), encoding);
            String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
                    encoding);
            boolean result = stringSign.equals(strAfterSha256);
            if(showLog){
                LogUtil.writeLog("验证签名" + (result ? "成功" : "失败"));
            }
            return result;
        } else if (SIGNMETHOD_SM3.equals(signMethod)) {
            // 1.进行SM3验证
            String stringSign = resData.get(SDKConstants.param_signature);
            if(showLog){
                LogUtil.writeLog("签名原文：[" + stringSign + "]");
            }
            // 将Map信息转换成key1=value1&key2=value2的形式
            String stringData = coverMap2String(resData);
            if(showLog){
                LogUtil.writeLog("待验签返回报文串：[" + stringData + "]");
            }
            String strBeforeSM3 = stringData
                    + SDKConstants.AMPERSAND
                    + SecureUtil.sm3X16Str(SDKConfig.getSecureKey(), encoding);
            String strAfterSM3 = SecureUtil
                    .sm3X16Str(strBeforeSM3, encoding);
            boolean result = stringSign.equals(strAfterSM3);
            LogUtil.writeLog("验证签名" + (result ? "成功" : "失败"));
            return result;
        }
        return false;
    }

    /**
     * 将Map中的数据转换成key1=value1&key2=value2的形式 不包含签名域signature
     *
     * @param data 待拼接的Map数据
     * @return 拼接好后的字符串
     */
    public static String coverMap2String(Map<String, String> data) {
        TreeMap<String, String> tree = new TreeMap<String, String>();
        Iterator<Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            if (SDKConstants.param_signature.equals(en.getKey().trim())) {
                continue;
            }
            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            sf.append(en.getKey() + SDKConstants.EQUAL + en.getValue()
                    + SDKConstants.AMPERSAND);
        }
        return sf.substring(0, sf.length() - 1);
    }


    /**
     * 将形如key=value&key=value的字符串转换为相应的Map对象
     *
     * @param result
     * @return
     */
    public static Map<String, String> convertResultStringToMap(String result) {
        Map<String, String> map = null;

        if (result != null && !"".equals(result.trim())) {
            if (result.startsWith("{") && result.endsWith("}")) {
                result = result.substring(1, result.length() - 1);
            }
            map = parseQString(result);
        }

        return map;
    }


    /**
     * 解析应答字符串，生成应答要素
     *
     * @param str 需要解析的字符串
     * @return 解析的结果map
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> parseQString(String str) {

        Map<String, String> map = new HashMap<String, String>();
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        char curChar;
        String key = null;
        boolean isKey = true;
        boolean isOpen = false;//值里有嵌套
        char openName = 0;
        if (len > 0) {
            for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
                curChar = str.charAt(i);// 取当前字符
                if (isKey) {// 如果当前生成的是key

                    if (curChar == '=') {// 如果读取到=分隔符
                        key = temp.toString();
                        temp.setLength(0);
                        isKey = false;
                    } else {
                        temp.append(curChar);
                    }
                } else {// 如果当前生成的是value
                    if (isOpen) {
                        if (curChar == openName) {
                            isOpen = false;
                        }

                    } else {//如果没开启嵌套
                        if (curChar == '{') {//如果碰到，就开启嵌套
                            isOpen = true;
                            openName = '}';
                        }
                        if (curChar == '[') {
                            isOpen = true;
                            openName = ']';
                        }
                    }

                    if (curChar == '&' && !isOpen) {// 如果读取到&分割符,同时这个分割符不是值域，这时将map里添加
                        putKeyValueToMap(temp, isKey, key, map);
                        temp.setLength(0);
                        isKey = true;
                    } else {
                        temp.append(curChar);
                    }
                }

            }
            putKeyValueToMap(temp, isKey, key, map);
        }
        return map;
    }

    private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
                                         String key, Map<String, String> map) {
        if (isKey) {
            key = temp.toString();
            if (key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }
            map.put(key, "");
        } else {
            if (key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }
            map.put(key, temp.toString());
        }
    }

    /**
     * 过滤请求报文中的空字符串或者空字符串
     *
     * @param contentData
     * @return
     */
    public static Map<String, String> filterBlank(Map<String, String> contentData) {
        LogUtil.writeLog("打印请求报文域 :");
        Map<String, String> submitFromData = new HashMap<String, String>();
        Set<String> keyset = contentData.keySet();

        for (String key : keyset) {
            String value = contentData.get(key);
            if (value != null && !"".equals(value.trim())) {
                // 对value值进行去除前后空处理
                submitFromData.put(key, value.trim());
                LogUtil.writeLog(key + "-->" + String.valueOf(value));
            }
        }
        return submitFromData;
    }

    /**
     * 判断字符串是否为NULL或空
     *
     * @param s 待判断的字符串数据
     * @return 判断结果 true-是 false-否
     */
    public static boolean isEmpty(String s) {
        return null == s || "".equals(s.trim());
    }

    public static String byteArrayToHexString(byte[] bytes) {
        return new String(Hex.encodeHex(bytes, true));
    }

}
