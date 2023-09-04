package com.alibaba.csp.sentinel.dashboard.encrypt;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("MyGenerator")
public class MyGenerator implements StringEncryptor {
    @Autowired
    private EncryptConfig encryptConfig;

    /**
     * 加密
     *
     * @param s 需加密内容
     * @return 加密后结果
     */
    @Override
    public String encrypt(String s) {
        return HsmEncryptor.getInstance(encryptConfig).encrypt(s);
    }

    /**
     * 解密
     *
     * @param s 需解密内容
     * @return 解密后结果
     */
    @Override
    public String decrypt(String s) {
        return HsmEncryptor.getInstance(encryptConfig).decrypt(s);
    }
}
