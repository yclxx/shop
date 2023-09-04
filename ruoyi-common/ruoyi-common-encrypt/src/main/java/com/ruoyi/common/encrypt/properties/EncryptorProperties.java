package com.ruoyi.common.encrypt.properties;

import com.ruoyi.common.encrypt.enumd.AlgorithmType;
import com.ruoyi.common.encrypt.enumd.EncodeType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加解密属性配置类
 *
 * @author 老马
 * @version 4.6.0
 */
@Data
@ConfigurationProperties(prefix = "mybatis-encryptor")
public class EncryptorProperties {

    /**
     * 过滤开关
     */
    private Boolean enable;

    /**
     * 默认算法
     */
    private AlgorithmType algorithm;

    /**
     * 安全秘钥
     */
    private String password;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 编码方式，base64/hex
     */
    private EncodeType encode;

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

    /**
     * 加密机连接数量
     */
    private String linkNum;
}
