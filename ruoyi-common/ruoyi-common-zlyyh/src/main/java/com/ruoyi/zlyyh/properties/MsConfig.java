package com.ruoyi.zlyyh.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 民生银行 配置属性
 *
 * @author Lion Li
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "msyh")
public class MsConfig {

    public String aesKey;

    public String payUrl;

    public String queryUrl;

    public String cancelUrl;

    public String certPwd;

    public String certPath;

    public String publicCertPath;

    public String redirectUrl;

    public String platformId;

    public String contractId;

}
