package com.ruoyi.common.core.utils;

import cn.hutool.core.codec.Base64;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Map;

/**
 * 功能：AES 工具类
 *
 * @author yzgnet
 * @date 2021
 */
public class AESUtils {
	private final static String KEY_ALGORITHMS = "AES";
	private final static int KEY_SIZE = 128;

	/**
	 * 生成AES密钥，base64编码格式 (128)
	 *
	 * @return 密钥
	 */
	public static String getKeyAES_128() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHMS);
		keyGen.init(KEY_SIZE);
		SecretKey key = keyGen.generateKey();
		return Base64Utils.encodeToString(key.getEncoded());
	}

	/**
	 * 根据base64Key获取SecretKey对象
	 */
	private static SecretKey loadKeyAES(String base64Key) {
		byte[] bytes = Base64Utils.decodeFromString(base64Key);
		return new SecretKeySpec(bytes, KEY_ALGORITHMS);
	}

	/**
	 * AES 加密字符串，base64Key对象
	 *
	 * @param base64Key   key
	 * @param encryptData 加密内容
	 * @param encode      编码
	 * @return 加密结果
	 */
	public static String encrypt(String base64Key, String encryptData, String encode) throws Exception {
		SecretKey key = loadKeyAES(base64Key);
		final Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptBytes = encryptData.getBytes(encode);
		byte[] result = cipher.doFinal(encryptBytes);
		return Base64Utils.encodeToString(result);
	}

	/**
	 * AES 解密字符串，base64Key对象
	 *
	 * @param base64Key   密钥
	 * @param decryptData 解密对象
	 * @param encode      编码
	 * @return 解密结果
	 */
	public static String decrypt(String base64Key, String decryptData, String encode) throws Exception {
		SecretKey key = loadKeyAES(base64Key);
		final Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptBytes = Base64Utils.decodeFromString(decryptData);
		byte[] result = cipher.doFinal(decryptBytes);
		return new String(result, encode);
	}

    /**
     * 微信 数据解密<br/>
     * 对称解密使用的算法为 AES-128-CBC，数据采用PKCS#7填充<br/>
     * 对称解密的目标密文:encrypted=Base64_Decode(encryptData)<br/>
     * 对称解密秘钥:key = Base64_Decode(session_key),aeskey是16字节<br/>
     * 对称解密算法初始向量:iv = Base64_Decode(iv),同样是16字节<br/>
     *
     * @param encrypted 目标密文
     * @param session_key 会话ID
     * @param iv 加密算法的初始向量
     */
    public static Map<String,Object> wxDecrypt(String encrypted, String session_key, String iv) {
        String json = null;
        byte[] encrypted64 = Base64.decode(encrypted);
        byte[] key64 = Base64.decode(session_key);
        byte[] iv64 = Base64.decode(iv);
        try {
            init();
            json = new String(decrypt(encrypted64, key64, generateIV(iv64)));
        } catch (Exception e) {
            System.out.println("解密微信手机号失败:" + e.getMessage());
        }
        return JsonUtils.parseMap(json);
    }

    /**
     * 微信小程序手机号 初始化密钥
     */
    private static void init() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyGenerator.getInstance(KEY_ALGORITHMS).init(KEY_SIZE);
    }

    /**
     * 微信小程序手机号 生成iv
     */
    private static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        // iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
        // Arrays.fill(iv, (byte) 0x00);
        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHMS);
        params.init(new IvParameterSpec(iv));
        return params;
    }

    /**
     * 微信小程序手机号 生成解密
     */
    private static byte[] decrypt(byte[] encryptedData, byte[] keyBytes, AlgorithmParameters iv)
        throws Exception {
        Key key = new SecretKeySpec(keyBytes, KEY_ALGORITHMS);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        // 设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(encryptedData);
    }
}
