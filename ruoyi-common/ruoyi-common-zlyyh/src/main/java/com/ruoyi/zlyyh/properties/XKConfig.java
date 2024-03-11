package com.ruoyi.zlyyh.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 携程接口 配置属性
 *
 * @author Lion Li
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "xiangku")
public class XKConfig {
    public String url;

    public String appId;

    public String appSecret;

    public String sourceType;

}
