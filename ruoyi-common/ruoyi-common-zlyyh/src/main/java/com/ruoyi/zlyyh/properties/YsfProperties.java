package com.ruoyi.zlyyh.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 云闪付接口 配置属性
 *
 * @author Lion Li
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "ysf")
public class YsfProperties {

    /**
     * 银联获取backendToken Url
     */
    private String backendTokenUrl;
    /**
     * 银联获取Token Url
     */
    private String tokenUrl;
    /**
     * 银联获取用户手机号 Url
     */
    private String userMobileUrl;
    /**
     * 62会员 会员中心sysId
     */
    private String sysId;
    /**
     * 会员中心 62会员查询接口
     */
    private String memberVipBalanceUrl;
    /**
     * # 会员中心 赠送62会员
     */
    private String memberVipAcquireUrl;
    /**
     * # 发放云闪付优惠券
     */
    private String sendCouponUrl;
    /**
     * # 查询云闪付优惠券
     */
    private String queryCouponUrl;
    /**
     * # 营销活动配置的赠送维度（参见营销平台活动配置） ，2位，可选：01-手机号 02-卡号 03-用户（三选一）
     */
    private String acctEntityTp = "03";
    /**
     * 云闪付积点扣除
     */
    private String memberPointDeductUrl;
    /**
     * 云闪付积点赠送
     */
    private String memberPointAcquireUrl;
    /**
     * 云闪付积点余额查询
     */
    private String memberPointBalanceUrl;
    /**
     * 积点获取渠道标识 接入方向用户忠诚度系统配置 扣减积点
     */
    private String memberPointDeductSource;
    /**
     * 积点获取渠道标识 接入方向用户忠诚度系统配置 赠送积点
     */
    private String memberPointAcquireSource;
    /**
     * AppId 这个AppID主要用来发放优惠券，积点，扣除积点等相关接口需要可以与平台AppId不一致
     */
    private String appId;
    /**
     * 密钥 主要用来发放优惠券，积点，扣除积点等相关接口
     */
    private String secret;
    /**
     * 对称密钥 主要用来发放优惠券，积点，扣除积点等相关接口
     */
    private String symmetricKey;
    /**
     * rsa签名私钥 主要用来发放优惠券，积点，扣除积点等相关接口
     */
    private String rsaPrivateKey;
    /**
     * 云闪付专享红包赠送URL
     */
    private String sendAcquireUrl;
    /**
     * 云闪付专享红包 机构账户代码，最大32位，对应云闪付小程序开放平台配置：营销能力包-红包接入方账户
     */
    private String insAcctId;

    /**
     * 云闪付任务体系业务类型
     */
    private String bizTp;
    /**
     * 云闪付任务体系业务子类型
     */
    private String subBizTp;
    /**
     * 云闪付任务体系报名URL
     */
    private String registerMissionUrl;
    /**
     * 云闪付任务体系进度查询URL
     */
    private String queryMissionUrl;
    /**
     * 初始化upsdk
     */
    private String getFrontToken;
}
