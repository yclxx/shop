package com.ruoyi.common.encrypt.jasypt;

import com.ruoyi.common.encrypt.core.HsmEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component("MyGenerator")
public class MyGenerator implements StringEncryptor {

    /**
     * 加密
     *
     * @param s 需加密内容
     * @return 加密后结果
     */
    @Override
    public String encrypt(String s) {
        return HsmEncryptor.getInstance().encrypt(s);
    }

    /**
     * 解密
     *
     * @param s 需解密内容
     * @return 解密后结果
     */
    @Override
    public String decrypt(String s) {
        return HsmEncryptor.getInstance().decrypt(s);
    }
}
