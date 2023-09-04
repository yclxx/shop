package com.ruoyi.common.encrypt.core;

import com.ruoyi.common.encrypt.enumd.AlgorithmType;
import com.ruoyi.common.encrypt.enumd.EncodeType;
import lombok.Data;

/**
 * 加密上下文 用于encryptor传递必要的参数。
 *
 * @author 老马
 * @version 4.6.0
 */
@Data
public class EncryptContext {

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

    /**
     * 编码方式，base64/hex
     */
    private EncodeType encode;

}
