package com.ruoyi.zlyyh.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "cloudrecharge")
public class CloudRechargeConfig {
    /**
     * appID
     */
    private String appId;
    /**
     * 密钥
     */
    private String appKey;
    /**
     * 对称密钥
     */
    private String aesKey;
    /**
     * 下单接口地址
     */
    private String submitUrl;
    /**
     * 查询订单接口地址
     */
    private String queryUrl;
    /**
     * 回调地址
     */
    private String callbackUrl;
}
