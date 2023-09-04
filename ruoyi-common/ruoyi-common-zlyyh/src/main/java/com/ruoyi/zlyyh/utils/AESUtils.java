package com.ruoyi.zlyyh.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

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
}
