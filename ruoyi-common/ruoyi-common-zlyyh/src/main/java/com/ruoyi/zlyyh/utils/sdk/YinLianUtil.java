package com.ruoyi.zlyyh.utils.sdk;

import cn.hutool.core.codec.Base64;
import com.ruoyi.common.core.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class YinLianUtil {
    private static final Logger logger = LoggerFactory.getLogger(YinLianUtil.class);

    /**
     * 生成签名随机字符串nonceStr
     *
     * @return
     */
    public static String createNonceStr() {
        String sl = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(sl.charAt(new Random().nextInt(sl.length())));
        }
        return sb.toString();
    }

    /**
     * 获取当前时间，秒
     *
     * @return
     */
    public static String createTimestamp() {
        long timeMillis = System.currentTimeMillis();
        timeMillis = timeMillis / 1000;
        return "" + timeMillis;
    }

    /**
     * @param data
     * @return
     */
    public static String sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return bytesToHex(md.digest(data));
        } catch (Exception ex) {
            logger.info("Never happen.", ex);
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        String hexArray = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            int bi = b & 0xff;
            sb.append(hexArray.charAt(bi >> 4));
            sb.append(hexArray.charAt(bi & 0xf));
        }
        return sb.toString();
    }

    public static String getDecryptedValue(String value, String key) throws Exception {
        if (null == value || "".equals(value)) {
            return "";
        }
        byte[] valueByte = Base64.decode(value);
        byte[] sl = decrypt3DES(valueByte, hexToBytes(key));
        return new String(sl);
    }

    public static byte[] decrypt3DES(byte[] input, byte[] key) throws Exception {
        Cipher c = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "DESede"));
        return c.doFinal(input);
    }

    public static byte[] hexToBytes(String hex) {
        hex = hex.length() % 2 != 0 ? "0" + hex : hex;

        byte[] b = new byte[hex.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(hex.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static String getDateFormYYYYMMDDhhmmss() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

	/**
	 * 当前时间加上多少分钟
	 * @param minutes 分钟
	 * @return 结果
	 */
	public static String getDateFormYYYYMMDDhhmmss(int minutes) {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(DateUtils.addMinutes(new Date(),minutes));
	}

    /**
     * 格式化时间
     * @param date 时间
     * @return 结果 yyyyMMddHHmmss
     */
    public static String getDateFormYYYYMMDDhhmmss(Date date) {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
    }
}
