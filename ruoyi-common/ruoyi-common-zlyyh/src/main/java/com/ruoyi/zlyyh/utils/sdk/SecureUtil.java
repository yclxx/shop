package com.ruoyi.zlyyh.utils.sdk;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.digests.SM3Digest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 *
 * @ClassName SecureUtil
 * @Description acpsdk安全算法工具类
 * @date 2016-7-22 下午4:08:32
 * 声明：以下代码只是为了方便接入方测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障
 */
public class SecureUtil {
	/**
	 * 算法常量： SHA1
	 */
	private static final String ALGORITHM_SHA1 = "SHA-1";
	/**
	 * 算法常量： SHA256
	 */
	private static final String ALGORITHM_SHA256 = "SHA-256";
	/**
	 * 算法常量：SHA1withRSA
	 */
	private static final String BC_PROV_ALGORITHM_SHA1RSA = "SHA1withRSA";
	/**
	 * 算法常量：SHA256withRSA
	 */
	private static final String BC_PROV_ALGORITHM_SHA256RSA = "SHA256withRSA";

	/**
	 * sm3计算后进行16进制转换
	 *
	 * @param data
	 *            待计算的数据
	 * @param encoding
	 *            编码
	 * @return 计算结果
	 */
	public static String sm3X16Str(String data, String encoding) {
		byte[] bytes = sm3(data, encoding);
		StringBuilder sm3StrBuff = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sm3StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				sm3StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		return sm3StrBuff.toString();
	}

	/**
	 * sha1计算后进行16进制转换
	 *
	 * @param data
	 *            待计算的数据
	 * @param encoding
	 *            编码
	 * @return 计算结果
	 */
	public static byte[] sha1X16(String data, String encoding) {
		byte[] bytes = sha1(data, encoding);
		StringBuilder sha1StrBuff = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sha1StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				sha1StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		try {
			return sha1StrBuff.toString().getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
			return null;
		}
	}


	/**
	 * sha256计算后进行16进制转换
	 *
	 * @param data
	 *            待计算的数据
	 * @param encoding
	 *            编码
	 * @return 计算结果
	 */
	public static String sha256X16Str(String data, String encoding) {
		byte[] bytes = sha256(data, encoding);
		StringBuilder sha256StrBuff = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sha256StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				sha256StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		return sha256StrBuff.toString();
	}

	/**
	 * sha256计算后进行16进制转换
	 *
	 * @param data
	 *            待计算的数据
	 * @param encoding
	 *            编码
	 * @return 计算结果
	 */
	public static byte[] sha256X16(String data, String encoding) {
		byte[] bytes = sha256(data, encoding);
		StringBuilder sha256StrBuff = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sha256StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				sha256StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		try {
			return sha256StrBuff.toString().getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * sha1计算.
	 *
	 *            待计算的数据
	 * @return 计算结果
	 */
	private static byte[] sha1(byte[] data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ALGORITHM_SHA1);
			md.reset();
			md.update(data);
			return md.digest();
		} catch (Exception e) {
			LogUtil.writeErrorLog("SHA1计算失败", e);
			return null;
		}
	}

	/**
	 * sha256计算.
	 *
	 *            待计算的数据
	 * @return 计算结果
	 */
	private static byte[] sha256(byte[] data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ALGORITHM_SHA256);
			md.reset();
			md.update(data);
			return md.digest();
		} catch (Exception e) {
			LogUtil.writeErrorLog("SHA256计算失败", e);
			return null;
		}
	}

	/**
	 * SM3计算.
	 *
	 *            待计算的数据
	 * @return 计算结果
	 */
	private static byte[] sm3(byte[] data) {
		SM3Digest sm3 = new SM3Digest();
		sm3.update(data, 0, data.length);
		byte[] result = new byte[sm3.getDigestSize()];
		sm3.doFinal(result, 0);
		return result;
	}

	/**
	 * sha1计算
	 *
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            字符集编码
	 * @return
	 */
	private static byte[] sha1(String datas, String encoding) {
		try {
			return sha1(datas.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog("SHA1计算失败", e);
			return null;
		}
	}

	/**
	 * sha256计算
	 *
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            字符集编码
	 * @return
	 */
	private static byte[] sha256(String datas, String encoding) {
		try {
			return sha256(datas.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog("SHA256计算失败", e);
			return null;
		}
	}

	/**
	 * sm3计算
	 *
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            字符集编码
	 * @return
	 */
	private static byte[] sm3(String datas, String encoding) {
		try {
			return sm3(datas.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog("SM3计算失败", e);
			return null;
		}
	}

	/**
	 *
	 * @param privateKey
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] signBySoft(PrivateKey privateKey, byte[] data)
			throws Exception {
		byte[] result = null;
		Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA1RSA, "BC");
		st.initSign(privateKey);
		st.update(data);
		result = st.sign();
		return result;
	}

	/**
	 * @param privateKey
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] signBySoft256(PrivateKey privateKey, byte[] data)
			throws Exception {
		byte[] result = null;
		Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA256RSA, "BC");
		st.initSign(privateKey);
		st.update(data);
		result = st.sign();
		return result;
	}

	public static boolean validateSignBySoft(PublicKey publicKey,
			byte[] signData, byte[] srcData) throws Exception {
		Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA1RSA, "BC");
		st.initVerify(publicKey);
		st.update(srcData);
		return st.verify(signData);
	}

	public static boolean validateSignBySoft256(PublicKey publicKey,
			byte[] signData, byte[] srcData) throws Exception {
		Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA256RSA, "BC");
		st.initVerify(publicKey);
		st.update(srcData);
		return st.verify(signData);
	}

	/**
	 * BASE64解码
	 *
	 * @param inputByte
	 *            待解码数据
	 * @return 解码后的数据
	 * @throws IOException
	 */
	public static byte[] base64Decode(byte[] inputByte) throws IOException {
		return Base64.decodeBase64(inputByte);
	}

	/**
	 * BASE64编码
	 *
	 * @param inputByte
	 *            待编码数据
	 * @return 解码后的数据
	 * @throws IOException
	 */
	public static byte[] base64Encode(byte[] inputByte) throws IOException {
		return Base64.encodeBase64(inputByte);
	}
}
