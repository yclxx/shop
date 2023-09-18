package com.ruoyi.common.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author yzgnet
 */
@Slf4j
public class RSAUtils {

    /**
     * 编码
     */
    public static final String PIK = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALpjHCNnOs1klUjppcqe0lwV+Qrjvgn37rhil9EwkrNDgjxCGUekWdMdw3xB8ethjFZiGYhxs79v/SCKSOLdf+YnWYnPdYuv7HvCnKRoS4d+SHgHZH8KxtFSk86pu1KIyk4WlJSN5br7/Jkvbqw5FuW07TgpZctZ2bTmnmXho4BpAgMBAAECgYAza8FpEQM7hArdfTxUnKF7b0JwWylkNacB7o1k1Io8c5z8A95WkgSIBneWkdjsr9JYSKMzre7Bm2NRtWTrVeGBREQ5UiWWAhIi9H2zgvg3A6Svtj2RwNQgmDzdy+wg1xyph3rzKO6xw8RgHTUHp3EHsLCt1FZ3VneWV1F7SFH86wJBAOkjLYt1KLMoozhnmzKtZ/AyHjs7PMVrQ+/ZnNvgbcxuL711CxeNPvIL4F5biV5Cub5v6JzBXsMz6vZjlIq/v5cCQQDMqkkBGE5R+nMEzL+hW0shjKaC7zbsjDBvM61r4GP4qZ2DqCWqOdW1K0N7EIEq6n+aes+4+9iHoEbSPJQ/Hr//AkEA1gCiiAbtazd8TAReo/AlHokC0yAXMqi53esFX5ftceAbFm/f1KilBQ390N95gvsBAVw8S9f8onZ/0deqvIoy1QJAF7vnm2jmLDuO+w+DaYLcw5c7+BMlm2jmdP7ZLZln/n4s9geZ1pO+ZLQPr0XKtN9czN1RGXKbOZ8sl1TPHELEoQJALFnGbUx43v+qTLl3ZTPOcHjvkmsFly1CPOf+Jb31UkOwb43rvVCKo8WXWkeh9DEI3xCOfHooIUO6Nh41eYmEGw==";
    public static final String PUK = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6YxwjZzrNZJVI6aXKntJcFfkK474J9+64YpfRMJKzQ4I8QhlHpFnTHcN8QfHrYYxWYhmIcbO/b/0gikji3X/mJ1mJz3WLr+x7wpykaEuHfkh4B2R/CsbRUpPOqbtSiMpOFpSUjeW6+/yZL26sORbltO04KWXLWdm05p5l4aOAaQIDAQAB";

    /**
     * 获取私钥
     *
     * @param privateKey 私钥字符串
     * @return
     */
    public static PrivateKey getPrivateKey(String privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.decode(privateKey.getBytes(StandardCharsets.UTF_8));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取公钥
     */
    public static PublicKey getPublicKey(String publicKey) {
        try {
            byte[] decodedKey = Base64.decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥加密
     *
     * @return
     */
    public static String encryptByPublicKey(String data, String publicKey) {
        return SecureUtil.rsa(null, publicKey).encryptBase64(data, KeyType.PublicKey);
    }

    /**
     * 私钥解密
     */
    public static String decryptByPrivateKey(String data, String privateKey) {
        return SecureUtil.rsa(privateKey, null).decryptStr(data, KeyType.PrivateKey);
    }

    /**
     * 签名
     *
     * @param key   私钥
     * @param value 请求参数
     * @return 签名
     */
    public static String sign(String key, String value) {
        return Base64.encode(SecureUtil.sign(SignAlgorithm.SHA256withRSA, key, null).sign(value));
    }

    public static boolean verifySign(String key, String value, String sign) {
        return SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, key).verify(value.getBytes(StandardCharsets.UTF_8), Base64.decode(sign));
    }
}
