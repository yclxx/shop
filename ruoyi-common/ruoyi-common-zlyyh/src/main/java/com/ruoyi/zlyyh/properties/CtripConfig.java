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
@ConfigurationProperties(prefix = "ctrip")
public class CtripConfig {

    public String getTokenUrl;

    public String refrshTokenUrl;

    public String url;

    public String aid;

    public String sid;

    public String key;

    public String partnerType;

    public String shopCode;

    public String shopProductCode;

    public String commodityInfoCode;

    public String productListCode;

    public String createOrderCode;

    public String productInfoCode;

    public String confirmOrderCode;

    public String cancelOrderCode;
}
