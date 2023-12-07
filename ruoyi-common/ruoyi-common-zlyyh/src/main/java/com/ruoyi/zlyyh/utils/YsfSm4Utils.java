package com.ruoyi.zlyyh.utils;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;
import java.util.Arrays;

/**
 * 银联sm4 加密
 */
public class YsfSm4Utils {
    // 固定填充byte数组
    private static String ivStr = "0123456789123456";
    //SM4-PADDING-CBC 运算方式
    public static final String SM4_CBC_PKCS7PADDING = "SM4/CBC/PKCS7Padding";

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        } else {
            Security.removeProvider("BC");
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * SM4加密算法
     *
     * @param value 加密字符
     * @param key   密钥
     * @return
     * @throws Exception
     */
    public static String encryptSM4(String value, String key) throws Exception {
        if (null == value || "".equals(value)) {
            return "";
        }
        byte[] valueByte = value.getBytes();
        byte[] result = sm4EncryptCBC(ByteUtils.hexToBytes(key), valueByte, ivStr.getBytes(),
            SM4_CBC_PKCS7PADDING);
        return Base64.encodeBase64String(result);
    }

    /**
     * SM4解密算法
     * @param value 待解密数据
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static String decryptSM4(String value, String key) throws Exception {
        if (null == value || "".equals(value)) {
            return "";
        }
        byte[] valueByte = Base64.decodeBase64(value);
        byte[] result =
            sm4DecryptCBC(ByteUtils.hexToBytes(key),valueByte,ivStr.getBytes(),
                SM4_CBC_PKCS7PADDING);
        return new String(result);
    }

    public static byte[] sm4DecryptCBC(byte[] keyBytes, byte[] cipher, byte[] iv, String algo){
        try {
            Key key = new SecretKeySpec(keyBytes, "SM4");
            Cipher in = Cipher.getInstance(algo, "BC");
            if(iv == null) iv = zeroIv(algo);
            in.init(Cipher.DECRYPT_MODE, key, getIV(iv));
            return in.doFinal(cipher);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] sm4EncryptCBC(byte[] keyBytes, byte[] data, byte[] iv, String algo) {
        try {
            Key key = new SecretKeySpec(keyBytes, "SM4");
            Cipher in = Cipher.getInstance(algo, "BC");
            if (iv == null) iv = zeroIv(algo);
            in.init(Cipher.ENCRYPT_MODE, key, getIV(iv));
            return in.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] zeroIv(final String algo) {
        try {
            Cipher cipher = Cipher.getInstance(algo);
            int blockSize = cipher.getBlockSize();
            byte[] iv = new byte[blockSize];
            Arrays.fill(iv, (byte) 0);
            return iv;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static IvParameterSpec getIV(byte[] iv) {
        return new IvParameterSpec(iv);
    }
}
