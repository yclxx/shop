package com.ruoyi.zlyyh.constant;

/**
 * 通用常量信息
 *
 * @author Lion Li
 */
public interface YsfUpConstants {
    /**
     * 银联开放平台 渠道ID
     */
    String chnlId = "chnlId";
    /**
     * 银联开放平台 AppID appId由开放平台网关分配，取值开放平台网关API认证账号
     */
    String appId = "appId";
    /**
     * 银联开放平台 手机号加密密钥
     */
    String sm4Key = "sm4Key";
    /**
     * 银联开放平台 签名私钥
     */
    String rsaPrivateKey = "rsaPrivateKey";
    /**
     * 银联开放平台 01：手机号维度；02：卡号维度；03：云闪付注册用户ID维度；05：外部用户ID维度；06：网络支付平台用户。07：证件号用户维度。票券的账户维度，同一个优惠券ID下的优惠券，其发放维度与核销维度一致，银行需确保支付交易中上送对应的要素。
     */
    String entityTp = "entityTp";
}
