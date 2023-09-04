package com.alibaba.csp.sentinel.dashboard.encrypt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "mybatis-encryptor")
@Component
public class EncryptConfig {

    /**
     * 加密机ip sm4银联加密机需要
     */
    private String hsmIp;

    /**
     * 加密机端口 sm4银联加密机需要
     */
    private String hsmPort;

    /**
     * 加密机密钥类型 sm4银联加密机需要
     */
    private String keyType;

    /**
     * 加密机密钥索引 sm4银联加密机需要
     */
    private Integer key;

    private String linkNum;

    public String getHsmIp() {
        return hsmIp;
    }

    public void setHsmIp(String hsmIp) {
        this.hsmIp = hsmIp;
    }

    public String getHsmPort() {
        return hsmPort;
    }

    public void setHsmPort(String hsmPort) {
        this.hsmPort = hsmPort;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getLinkNum() {
        return linkNum;
    }

    public void setLinkNum(String linkNum) {
        this.linkNum = linkNum;
    }
}
