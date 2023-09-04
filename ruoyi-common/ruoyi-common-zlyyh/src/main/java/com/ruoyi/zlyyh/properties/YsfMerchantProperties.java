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
@ConfigurationProperties(prefix = "ysfmerchant")
public class YsfMerchantProperties {
    /**
     * appId
     */
    private String appId;
    /**
     * 渠道ID
     */
    private String chnnlId;
    /**
     *signId
     */
    private String signId;
    /**
     * token
     */
    private String token;
    /**
     * 发送机构代码
     */
    private String openInsId;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 品牌方法
     */
    private String brandMethod;
    /**
     * 门店方法
     */
    private String storeMethod;
    /**
     * 证书密码
     */
    private String certPwd;
    /**
     * 证书路径
     */
    private String certPath;

}
