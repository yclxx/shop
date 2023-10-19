package com.ruoyi.zlyyh.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 微信 配置属性
 *
 * @author Lion Li
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "wx")
public class WxProperties {

    /** 登录凭证校验,获取openID */
    private String codeSessionUrl;

    /** 获取手机号 */
    private String userPhoneNumberUrl;

    /** 微信基础访问令牌 */
    private String accessTokenUrl;
}
