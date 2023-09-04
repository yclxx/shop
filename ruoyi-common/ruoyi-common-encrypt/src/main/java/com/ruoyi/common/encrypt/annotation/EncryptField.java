package com.ruoyi.common.encrypt.annotation;

import com.ruoyi.common.encrypt.enumd.AlgorithmType;
import com.ruoyi.common.encrypt.enumd.EncodeType;

import java.lang.annotation.*;

/**
 * 字段加密注解
 *
 * @author 老马
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

    /**
     * 加密算法
     */
    AlgorithmType algorithm() default AlgorithmType.DEFAULT;

    /**
     * 秘钥。AES、SM4需要
     */
    String password() default "";

    /**
     * 公钥。RSA、SM2需要
     */
    String publicKey() default "";

    /**
     * 公钥。RSA、SM2需要
     */
    String privateKey() default "";

    /**
     * 加密机ip sm4银联加密机需要
     */
    String hsmIp() default "";

    /**
     * 加密机端口 sm4银联加密机需要
     */
    String hsmPort() default "";

    /**
     * 加密机密钥类型 sm4银联加密机需要
     */
    String keyType() default "";

    /**
     * 加密机密钥索引 sm4银联加密机需要
     */
    int key() default -99;

    /**
     * 编码方式。对加密算法为BASE64的不起作用
     */
    EncodeType encode() default EncodeType.DEFAULT;

}
