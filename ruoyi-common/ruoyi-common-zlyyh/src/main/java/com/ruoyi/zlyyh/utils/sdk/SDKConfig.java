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
 * xshu       2014-05-28       MPI基本参数工具类
 * =============================================================================
 */
package com.ruoyi.zlyyh.utils.sdk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @ClassName SDKConfig
 * @Description acpsdk配置文件acp_sdk.properties配置信息类
 * @date 2016-7-22 下午4:04:55
 * 声明：以下代码只是为了方便接入方测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障
 */
@PropertySource(value = {"classpath:acp_sdk.properties"})
@Component
@ConfigurationProperties(prefix = "acpsdk")
public class SDKConfig {
    public static final String FILE_NAME = "acp_sdk.properties";
    /** 配置文件中签名证书路径常量. */
    public static final String SDK_SIGNCERT_PATH = "acpsdk.signCert.path";
    /** 配置文件中签名证书密码常量. */
    public static final String SDK_SIGNCERT_PWD = "acpsdk.signCert.pwd";
    /** 配置文件中签名证书类型常量. */
    public static final String SDK_SIGNCERT_TYPE = "acpsdk.signCert.type";
    /** 配置文件中根证书路径常量  */
    public static final String SDK_ROOTCERT_PATH = "acpsdk.rootCert.path";
    /** 配置文件中根证书路径常量  */
    public static final String SDK_MIDDLECERT_PATH = "acpsdk.middleCert.path";
    /**
     * 商户号
     */
    private static String merId;
    /**
     * 前台请求URL.
     */
    private static String frontTransUrl;
    /**
     * 后台请求URL.
     */
    private static String backTransUrl;
    /** app交易 */
    private static String appTransUrl;
    /**
     * 单笔查询
     */
    private static String singleQueryUrl;
    /**
     * 签名证书路径.
     */
    private static String signCertPath;
    /**
     * 签名证书密码.
     */
    private static String signCertPwd;
    /**
     * 签名证书类型.
     */
    private static String signCertType;
    /**
     * 加密公钥证书路径.
     */
    private static String encryptCertPath;
    /**
     * 中级证书路径
     */
    private static String middleCertPath;
    /**
     * 根证书路径
     */
    private static String rootCertPath;
    /**
     * 是否验证验签证书CN，除了false都验
     */
    private static boolean ifValidateCNName = true;
    private static String secureKey;
    /**
     * 是否验证https证书，默认都不验
     */
    private static boolean ifValidateRemoteCert;
    /**
     * signMethod，没配按01吧
     */
    private static String signMethod;
    /**
     * version，没配按5.0.0
     */
    private static String version;
    /**
     * frontUrl
     */
    private static String frontUrl;
    /**
     * backUrl
     */
    private static String backUrl;
    /**
     * backUrlTrans
     */
    private static String backUrlTrans;

    public static String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        SDKConfig.merId = merId;
    }

    public static String getFrontTransUrl() {
        return frontTransUrl;
    }

    public void setFrontTransUrl(String frontTransUrl) {
        SDKConfig.frontTransUrl = frontTransUrl;
    }

    public static String getBackTransUrl() {
        return backTransUrl;
    }

    public void setBackTransUrl(String backTransUrl) {
        SDKConfig.backTransUrl = backTransUrl;
    }

    public static String getSingleQueryUrl() {
        return singleQueryUrl;
    }

    public void setSingleQueryUrl(String singleQueryUrl) {
        SDKConfig.singleQueryUrl = singleQueryUrl;
    }

    public static String getSignCertPath() {
        return signCertPath;
    }

    public void setSignCertPath(String signCertPath) {
        SDKConfig.signCertPath = signCertPath;
    }

    public static String getSignCertPwd() {
        return signCertPwd;
    }

    public void setSignCertPwd(String signCertPwd) {
        SDKConfig.signCertPwd = signCertPwd;
    }

    public static String getSignCertType() {
        return signCertType;
    }

    public void setSignCertType(String signCertType) {
        SDKConfig.signCertType = signCertType;
    }

    public static String getEncryptCertPath() {
        return encryptCertPath;
    }

    public void setEncryptCertPath(String encryptCertPath) {
        SDKConfig.encryptCertPath = encryptCertPath;
    }

    public static String getMiddleCertPath() {
        return middleCertPath;
    }

    public void setMiddleCertPath(String middleCertPath) {
        SDKConfig.middleCertPath = middleCertPath;
    }

    public static String getRootCertPath() {
        return rootCertPath;
    }

    public void setRootCertPath(String rootCertPath) {
        SDKConfig.rootCertPath = rootCertPath;
    }

    public static boolean isIfValidateCNName() {
        return ifValidateCNName;
    }

    public void setIfValidateCNName(boolean ifValidateCNName) {
        SDKConfig.ifValidateCNName = ifValidateCNName;
    }

    public static boolean isIfValidateRemoteCert() {
        return ifValidateRemoteCert;
    }

    public void setIfValidateRemoteCert(boolean ifValidateRemoteCert) {
        SDKConfig.ifValidateRemoteCert = ifValidateRemoteCert;
    }

    public static String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        SDKConfig.signMethod = signMethod;
    }

    public static String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        SDKConfig.version = version;
    }

    public static String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        SDKConfig.frontUrl = frontUrl;
    }

    public static String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        SDKConfig.backUrl = backUrl;
    }

    public static String getBackUrlTrans() {
        return backUrlTrans;
    }

    public void setBackUrlTrans(String backUrlTrans) {
        SDKConfig.backUrlTrans = backUrlTrans;
    }

    public static String getAppTransUrl() {
        return appTransUrl;
    }

    public void setAppTransUrl(String appTransUrl) {
        SDKConfig.appTransUrl = appTransUrl;
    }

    public static String getSecureKey() {
        return secureKey;
    }

    public void setSecureKey(String secureKey) {
        SDKConfig.secureKey = secureKey;
    }
}
