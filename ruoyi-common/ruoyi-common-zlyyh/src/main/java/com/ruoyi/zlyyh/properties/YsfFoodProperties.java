package com.ruoyi.zlyyh.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 云闪付美食接口 配置属性
 *
 * @author Lion Li
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "ysffood")
public class YsfFoodProperties {
    /**
     * rsa签名私钥
     */
    private String rsaPrivateKey;
    /**
     * appId
     */
    private String appId;
    /**
     * 品牌列表url
     */
    private String brandListUrl;
    /**
     * 门店商品url
     */
    private String productListUrl;
    /**
     * 门店详情url
     */
    private String shopInfoUrl;
    /**
     * 商品详情url
     */
    private String productInfoUrl;
    /**
     * 新增订单url
     */
    private String insertOrderUrl;
    /**
     * 支付订单url
     */
    private String payOrderUrl;
    /**
     * 查询订单url
     */
    private String queryOrderUrl;
    /**
     * 申请退款url
     */
    private String refundUrl;

    /**
     * 消息发送接口
     */
    private String sendMessageUrl;
}
