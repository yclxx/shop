package com.ruoyi.common.encrypt.core.encryptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import com.ruoyi.common.encrypt.core.EncryptContext;
import com.ruoyi.common.encrypt.enumd.AlgorithmType;
import com.ruoyi.common.encrypt.enumd.EncodeType;

import java.nio.charset.StandardCharsets;

/**
 * sm4算法实现
 *
 * @author 老马
 * @version 4.6.0
 */
public class Sm4Encryptor extends AbstractEncryptor {

    private final SM4 sm4;

    public Sm4Encryptor(EncryptContext context) {
        super(context);
        String password = context.getPassword();
        if (StrUtil.isBlank(password)) {
            throw new IllegalArgumentException("SM4没有获得秘钥信息");
        }
        // sm4算法的秘钥要求是16位长度
        if (16 != password.length()) {
            throw new IllegalArgumentException("SM4秘钥长度应该为16位，实际为" + password.length() + "位");
        }
        this.sm4 = SmUtil.sm4(password.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.SM4;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        if (encodeType == EncodeType.HEX) {
            return sm4.encryptHex(value);
        } else {
            return sm4.encryptBase64(value);
        }
    }

    /**
     * 解密
     *
     * @param value      待加密字符串
     */
    @Override
    public String decrypt(String value) {
        return this.sm4.decryptStr(value);
    }

    public static void main(String[] args) {
        String s = SmUtil.sm4("Yzgnet.com#123_1".getBytes(StandardCharsets.UTF_8)).decryptStr("FCq+L4RmZSL5Zvmpd7GDmQ==");
        System.out.println(s);
        s = SmUtil.sm4("Yzgnet.com#123_1".getBytes(StandardCharsets.UTF_8)).decryptStr("MJ5DOYt3BURyQ6WSGmBJXg==");
        System.out.println(s);
        s =SmUtil.sm4("Yzgnet.com#123_1".getBytes(StandardCharsets.UTF_8)).decryptStr("GhsLuFu2jmHkcTMKDKVf/A==");
        System.out.println(s);
        s =SmUtil.sm4("Yzgnet.com#123_1".getBytes(StandardCharsets.UTF_8)).decryptStr("jKSIsSjwHMosqI3ejBpwBQ==");
        System.out.println(s);
        s =SmUtil.sm4("Yzgnet.com#123_1".getBytes(StandardCharsets.UTF_8)).decryptStr("zBnH7dvMfkl/ggSGOHwHmA==");
        System.out.println(s);
        s =SmUtil.sm4("Yzgnet.com#123_1".getBytes(StandardCharsets.UTF_8)).decryptStr("HCTvPjwexkzp7tvmZ1plLQ==");
        System.out.println(s);
        s =SmUtil.sm4("Yzgnet.com#123_1".getBytes(StandardCharsets.UTF_8)).decryptStr("yaEDPlBHOY4k2qqj+8UiwA==");
        System.out.println(s);
    }
}
