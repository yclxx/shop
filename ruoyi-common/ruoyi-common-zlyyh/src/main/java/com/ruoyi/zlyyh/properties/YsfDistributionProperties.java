package com.ruoyi.zlyyh.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 云闪付内容分销（渠道方） 配置书香
 *
 * @author hyh
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "ysfdistribution")
public class YsfDistributionProperties {
    /**
     * 渠道商户号
     */
    private String JDAppId;
    private String JCAppId;
    /**
     * 域名
     */
    private String url;

    /**
     * 证书密码
     */
    private String certPwd;
    /**
     * 证书路径(JD 直销)
     */
    private String certPathJD;
    /**
     * 证书路径(JC 代销)
     */
    private String certPathJC;

    /**
     * 支付回调地址
     */
    private String backUrl;
}
