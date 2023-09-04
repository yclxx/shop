package com.ruoyi.zlyyh.utils.sdk;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UnionPayMerchantUtil {
    private static ConcurrentHashMap<String, MerKey> signCertsPathMap = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, MerKey> signCertsSnMap = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, X509Certificate> verifyCerts510 = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, MerKey> verifyCertsSnMap = new ConcurrentHashMap();
    private static X9ECParameters x9ECParameters = GMNamedCurves.getByName("sm2p256v1");
    private static ECDomainParameters ecDomainParameters;
    private static ECParameterSpec ecParameterSpec;

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        } else {
            Security.removeProvider("BC");
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    static {
        ecDomainParameters = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN());
        ecParameterSpec = new ECParameterSpec(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN());
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        } else {
            Security.removeProvider("BC");
            Security.addProvider(new BouncyCastleProvider());
        }

    }

    /**
     * 获取商户接口
     * @param
     */
    public static String getBrandList(Integer pageSize, Integer pageNum, String appId, String chnnlId, String signId, String token, String bizMethod, String openInsId, String certPath, String certPwd, String url) {
        HashMap<String, String> reqData = new HashMap<>();
        // 组装HTTP请求报文头
        reqData.put("content-type", "application/json;charset=utf-8");
        reqData.put("Accept", "application/json");
        reqData.put("Accept-Charset", "utf-8");
        reqData.put(PersonalizedSDKConstants.param_bizMethod, bizMethod);
        reqData.put(PersonalizedSDKConstants.param_version, "1.0.0");
        //正式
        reqData.put("appType", "01");
        reqData.put(PersonalizedSDKConstants.param_appId, appId);// 机构id
        long snowflakeNextId = IdUtil.getSnowflakeNextId();
        reqData.put(PersonalizedSDKConstants.param_reqId, String.valueOf(snowflakeNextId));      // 发送方流水号，可以自行定制规则
        reqData.put(PersonalizedSDKConstants.param_reqTs, DateUtils.createTimestampStr(true));
        reqData.put(PersonalizedSDKConstants.param_signId, signId);     // tokenId
        reqData.put(PersonalizedSDKConstants.param_token, token);      // token
        reqData.put(PersonalizedSDKConstants.param_signMethod, "SM2");      // 签名方法，固定填写
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = new HashMap<>();
        //正式
        bodyMap.put("openInsId", openInsId);
        bodyMap.put("chnnlId", chnnlId);
        long snowId = IdUtil.getSnowflakeNextId();
        bodyMap.put("insTraceId", String.valueOf(snowId));
        String s = DateUtils.dateTimeNow();
        String yyyymmdd = s.substring(0, 8);
        String hhmmss = s.substring(8, 14);
        bodyMap.put("orderDt", yyyymmdd);
        bodyMap.put("orderTm", hhmmss);
        bodyMap.put("pageNum", pageNum);
        bodyMap.put("pageSize", pageSize);

        reqData.put(PersonalizedSDKConstants.param_body, JSON.toJSONString(bodyMap));

        //签名
        Map<String, String> reqMap = sign(reqData, certPath, certPwd);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        reqMap.remove(PersonalizedSDKConstants.param_token);
        String result = HttpUtil.createPost(url).addHeaders(reqMap).body(JSON.toJSONString(bodyMap)).execute().body();
        if (ObjectUtil.isEmpty(result)) {
            return null;
        }
        return result;

    }

    /**
     * 获取商户图片接口
     * @param
     */
    public static String getBrandImg(String appId, String chnnlId, String signId, String token, String bizMethod, String openInsId, String certPath, String certPwd, String url) {
        HashMap<String, String> reqData = new HashMap<>();
        // 组装HTTP请求报文头
        reqData.put("content-type", "application/json;charset=utf-8");
        reqData.put("Accept", "application/json");
        reqData.put("Accept-Charset", "utf-8");
        reqData.put(PersonalizedSDKConstants.param_bizMethod, bizMethod);
        reqData.put(PersonalizedSDKConstants.param_version, "1.0.0");
        //正式
        reqData.put("appType", "01");
        reqData.put(PersonalizedSDKConstants.param_appId, appId);// 机构id
        long snowflakeNextId = IdUtil.getSnowflakeNextId();
        reqData.put(PersonalizedSDKConstants.param_reqId, String.valueOf(snowflakeNextId));      // 发送方流水号，可以自行定制规则
        reqData.put(PersonalizedSDKConstants.param_reqTs, DateUtils.createTimestampStr(true));
        reqData.put(PersonalizedSDKConstants.param_signId, signId);     // tokenId
        reqData.put(PersonalizedSDKConstants.param_token, token);      // token
        reqData.put(PersonalizedSDKConstants.param_signMethod, "SM2");      // 签名方法，固定填写
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = new HashMap<>();
        //正式
        bodyMap.put("openInsId", openInsId);
        bodyMap.put("chnnlId", chnnlId);
        long snowId = IdUtil.getSnowflakeNextId();
        bodyMap.put("insTraceId", String.valueOf(snowId));
        String s = DateUtils.dateTimeNow();
        String yyyymmdd = s.substring(0, 8);
        String hhmmss = s.substring(8, 14);
        bodyMap.put("orderDt", yyyymmdd);
        bodyMap.put("orderTm", hhmmss);

        reqData.put(PersonalizedSDKConstants.param_body, JSON.toJSONString(bodyMap));

        //签名
        Map<String, String> reqMap = sign(reqData, certPath, certPwd);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        reqMap.remove(PersonalizedSDKConstants.param_token);
        String result = HttpUtil.createPost(url).addHeaders(reqMap).body(JSON.toJSONString(bodyMap)).execute().body();
        if (ObjectUtil.isEmpty(result)) {
            return null;
        }
        return result;

    }


    /**
     * 获取门店接口*
     * * @param args
     */
    public static String getShopList(Integer pageSize, Integer pageNum, String brandId, String appId, String chnnlId, String signId, String token, String bizMethod, String openInsId, String certPath, String certPwd, String url) {
        HashMap<String, String> reqData = new HashMap<>();
        // 组装HTTP请求报文头
        reqData.put("content-type", "application/json;charset=utf-8");
        reqData.put("Accept", "application/json");
        reqData.put("Accept-Charset", "utf-8");
        reqData.put(PersonalizedSDKConstants.param_bizMethod, bizMethod);
        reqData.put(PersonalizedSDKConstants.param_version, "1.0.0");
        //正式
        reqData.put("appType", "01");
        reqData.put(PersonalizedSDKConstants.param_appId, appId);// 在中台门户登记的服务使用方系统名称英文缩写
        long snowflakeNextId = IdUtil.getSnowflakeNextId();
        reqData.put(PersonalizedSDKConstants.param_reqId, String.valueOf(snowflakeNextId));      // 发送方流水号，可以自行定制规则
        reqData.put(PersonalizedSDKConstants.param_reqTs, DateUtils.createTimestampStr(true));
        reqData.put(PersonalizedSDKConstants.param_signId, signId);     // tokenId
        reqData.put(PersonalizedSDKConstants.param_token, token);      // token
        reqData.put(PersonalizedSDKConstants.param_signMethod, "SM2");      // 签名方法，固定填写
        // 组装HTTP请求报文体 根据实际业务填写
        Map<String, Object> bodyMap = new HashMap<>();
        //正式
        bodyMap.put("openInsId", openInsId);
        bodyMap.put("chnnlId", chnnlId);
        long snowId = IdUtil.getSnowflakeNextId();
        bodyMap.put("insTraceId", String.valueOf(snowId));
        String s = DateUtils.dateTimeNow();
        String yyyymmdd = s.substring(0, 8);
        String hhmmss = s.substring(8, 14);
        bodyMap.put("orderDt", yyyymmdd);
        bodyMap.put("orderTm", hhmmss);
        bodyMap.put("pageNum", pageNum);
        bodyMap.put("pageSize", pageSize);
        bodyMap.put("brandId", brandId);
        bodyMap.put("storeCityDivisNm", "金华市");
        bodyMap.put("bdId", "");

        reqData.put(PersonalizedSDKConstants.param_body, JSON.toJSONString(bodyMap));

        //签名
        Map<String, String> reqMap = sign(reqData, certPath, certPwd);
        reqMap.remove(PersonalizedSDKConstants.param_body);
        reqMap.remove(PersonalizedSDKConstants.param_token);
        String result = HttpUtil.createPost(url).addHeaders(reqMap).body(JSON.toJSONString(bodyMap)).execute().body();
        if (ObjectUtil.isEmpty(result)) {
            return null;
        }
        return result;

    }

    //public static void main(String[] args) {
    //    String str = "04EFFD8C48F81C2170B95C3D3D809AAE98237B6E6855B98B523926D6DDD34F23F8543E60E08CF3626913DFAD3927A4D5E7C8747DEDCEDDA37F0FE88D03F726892E54C2EC18ADA41EBDF21CD28D6259065A2B235E858D19E72BC9E7FB824514DA9AB5BDFE5EDB81B61D2E6C";
    //    String s = sm2Decrypt(str, "cert/8983199799707JD.sm2", "123123");
    //    System.out.println(s);
    //}
    public static Map<String, String> sign(Map<String, String> reqData, String certPath, String certPwd) {
        return signByCertInfo(reqData, certPath, certPwd);
    }

    public static String sm2Decrypt(String bondNo, String certPath, String certPwd) {
        SM2 sm2 = SmUtil.sm2();
        PrivateKey signCertPrivateKey = getSignCertPrivateKey(certPath, certPwd);
        sm2.setPrivateKey(signCertPrivateKey);
        return StrUtil.utf8Str(sm2.decryptFromBcd(bondNo, KeyType.PrivateKey));
    }


    public static Map<String, String> signByCertInfo(Map<String, String> reqData, String certPath, String certPwd) {
        Map<String, String> data = SDKUtil.filterBlank(reqData);
        if (!SDKUtil.isEmpty(certPath) && !SDKUtil.isEmpty(certPwd)) {
            try {
                data.put("signId", getCertIdByKeyStoreMap(certPath, certPwd));
                String stringData = getSignStr(data);
                log.info("打印排序后待签名请求报文串: [" + stringData + "]\n");
                String sign = Base64.encodeBase64String(signSm3WithSm2(stringData.getBytes(), getCertIdByKeyStoreMap(certPath, certPwd).getBytes("utf-8"), getSignCertPrivateKey(certPath, certPwd)));
                data.put("sign", sign);
                return data;
            } catch (Exception var6) {
                log.error("Sign Error", var6);
                return data;
            }
        } else {
            log.error("CertPath or CertPwd is empty");
            return data;
        }
    }

    public static byte[] signSm3WithSm2(byte[] msg, byte[] userId, PrivateKey privateKey) {
        return rsAsn1ToPlainByteArray(signSm3WithSm2Asn1Rs(msg, userId, privateKey));
    }

    public static PrivateKey getSignCertPrivateKey(String path, String pwd) {
        return getSignKeyByPath(path, pwd).getPriKey();
    }


    private static byte[] rsAsn1ToPlainByteArray(byte[] rsDer) {
        ASN1Sequence seq = ASN1Sequence.getInstance(rsDer);
        byte[] r = bigIntToFixexLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(0)).getValue());
        byte[] s = bigIntToFixexLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(1)).getValue());
        byte[] result = new byte[64];
        System.arraycopy(r, 0, result, 0, r.length);
        System.arraycopy(s, 0, result, 32, s.length);
        return result;
    }

    public static byte[] signSm3WithSm2Asn1Rs(byte[] msg, byte[] userId, PrivateKey privateKey) {
        try {
            SM2ParameterSpec parameterSpec = new SM2ParameterSpec(userId);
            Signature signer = Signature.getInstance("SM3withSM2", "BC");
            signer.setParameter(parameterSpec);
            signer.initSign(privateKey, new SecureRandom());
            signer.update(msg, 0, msg.length);
            byte[] sig = signer.sign();
            return sig;
        } catch (Exception var6) {
            throw new RuntimeException(var6);
        }
    }

    private static byte[] bigIntToFixexLengthBytes(BigInteger rOrS) {
        byte[] rs = rOrS.toByteArray();
        if (rs.length == 32) {
            return rs;
        } else if (rs.length == 33 && rs[0] == 0) {
            return Arrays.copyOfRange(rs, 1, 33);
        } else if (rs.length < 32) {
            byte[] result = new byte[32];
            Arrays.fill(result, (byte) 0);
            System.arraycopy(rs, 0, result, 32 - rs.length, rs.length);
            return result;
        } else {
            throw new RuntimeException("err rs: " + Hex.toHexString(rs));
        }
    }


    private static String getSignStr(Map<String, String> data) {
        return "version=" + (String) data.get("version") + "&" + "appId" + "=" + (String) data.get("appId") + "&" + "bizMethod" + "=" + (String) data.get("bizMethod") + "&" + "reqId" + "=" + (String) data.get("reqId") + "&" + "body" + "=" + (String) data.get("body");
    }

    public static String getCertIdByKeyStoreMap(String path, String pwd) {
        return getSignKeyByPath(path, pwd).getCertId();
    }

    private static MerKey getSignKeyByPath(String certFilePath, String certPwd) {
        if (certFilePath != null && certPwd != null) {
            if (!signCertsPathMap.containsKey(certFilePath)) {
                MerKey key = readGmPriCert(certFilePath, certPwd);
                if (key != null) {
                    log.info("从 [" + certFilePath + "] 读取签名私钥成功 [" + key.getCertId() + "]");
                    signCertsPathMap.put(certFilePath, key);
                    signCertsSnMap.put(key.getCertId(), key);
                }
            }

            return (MerKey) signCertsPathMap.get(certFilePath);
        } else {
            throw new IllegalArgumentException("null argument");
        }
    }

    private static MerKey readGmPriCert(String pfxkeyfile, String pwd) {
        ClassPathResource classPathResource = new ClassPathResource(pfxkeyfile);

        Sm2Cert cert;
        try {

            byte[] bs = IOUtils.toByteArray(classPathResource.getInputStream());
            bs = Base64.decodeBase64(bs);
            cert = readSm2File(bs, pwd);
            MerKey key = new MerKey();
            key.setCertId((new BigInteger(cert.getCertId(), 10)).toString(10));
            key.setPriKey(cert.getPrivateKey());
            MerKey var6 = key;
            return var6;
        } catch (Exception var16) {
            log.error("getKeyInfo Error [" + pfxkeyfile + "]", var16);

        }
        return null;
    }

    public static Sm2Cert readSm2File(byte[] pem, String pwd) {
        Sm2Cert sm2Cert = new Sm2Cert();

        try {
            ASN1Sequence asn1Sequence = (ASN1Sequence) ASN1Primitive.fromByteArray(pem);
            ASN1Sequence priSeq = (ASN1Sequence) asn1Sequence.getObjectAt(1);
            ASN1Sequence pubSeq = (ASN1Sequence) asn1Sequence.getObjectAt(2);
            ASN1OctetString priKeyAsn1 = (ASN1OctetString) priSeq.getObjectAt(2);
            byte[] key = KDF(pwd.getBytes(), 32);
            byte[] priKeyD = sm4DecryptCBC(Arrays.copyOfRange(key, 16, 32), priKeyAsn1.getOctets(), Arrays.copyOfRange(key, 0, 16), "SM4/CBC/PKCS7Padding");
            sm2Cert.privateKey = getPrivatekeyFromD(new BigInteger(1, priKeyD));
            ASN1OctetString pubKeyX509 = (ASN1OctetString) pubSeq.getObjectAt(1);
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            X509Certificate x509 = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(pubKeyX509.getOctets()));
            sm2Cert.publicKey = x509.getPublicKey();
            sm2Cert.certId = x509.getSerialNumber().toString();
            return sm2Cert;
        } catch (Exception var12) {
            throw new RuntimeException(var12);
        }
    }

    public static BCECPrivateKey getPrivatekeyFromD(BigInteger d) {
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(d, ecParameterSpec);
        return new BCECPrivateKey("EC", ecPrivateKeySpec, BouncyCastleProvider.CONFIGURATION);
    }

    public static byte[] sm4DecryptCBC(byte[] keyBytes, byte[] cipher, byte[] iv, String algo) {
        try {
            Key key = new SecretKeySpec(keyBytes, "SM4");
            Cipher in = Cipher.getInstance(algo, "BC");
            if (iv == null) {
                iv = zeroIv(algo);
            }

            in.init(2, key, getIV(iv));
            return in.doFinal(cipher);
        } catch (Exception var6) {
            return null;
        }
    }

    public static byte[] zeroIv(String algo) {
        try {
            Cipher cipher = Cipher.getInstance(algo);
            int blockSize = cipher.getBlockSize();
            byte[] iv = new byte[blockSize];
            Arrays.fill(iv, (byte) 0);
            return iv;
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static IvParameterSpec getIV(byte[] iv) {
        return new IvParameterSpec(iv);
    }

    private static byte[] KDF(byte[] Z, int klen) {
        int ct = 1;
        int end = (int) Math.ceil((double) klen * 1.0D / 32.0D);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            for (int i = 1; i < end; ++i) {
                baos.write(sm3(join(Z, toByteArray(ct))));
                ++ct;
            }

            byte[] last = sm3(join(Z, toByteArray(ct)));
            if (klen % 32 == 0) {
                baos.write(last);
            } else {
                baos.write(last, 0, klen % 32);
            }

            return baos.toByteArray();
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

    private static byte[] join(byte[]... params) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] res = null;

        try {
            for (int i = 0; i < params.length; ++i) {
                baos.write(params[i]);
            }

            res = baos.toByteArray();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return res;
    }

    public static byte[] sm3(byte[] bytes) {
        SM3Digest sm3 = new SM3Digest();
        sm3.update(bytes, 0, bytes.length);
        byte[] result = new byte[sm3.getDigestSize()];
        sm3.doFinal(result, 0);
        return result;
    }

    private static byte[] toByteArray(int i) {
        byte[] byteArray = new byte[]{(byte) (i >>> 24), (byte) ((i & 16777215) >>> 16), (byte) ((i & '\uffff') >>> 8), (byte) (i & 255)};
        return byteArray;
    }

    //验签
    public static boolean validate(Map<String, String> data) {
        log.info("验证银联签名处理开始");
        String certId = (String) data.get("signId");
        log.info("对返回报文串验签使用的银联验签公钥序列号：[" + certId + "]");
        PublicKey verifyKey;
        String stringSign;
        if (data.get("signPubKeyCert") != null) {
            stringSign = (String) data.get("signPubKeyCert");

            try {
                X509Certificate x509Certificate = verifyAndGetVerifyPubKey(URLDecoder.decode(stringSign, "UTF-8"));
                verifyKey = x509Certificate.getPublicKey();
            } catch (Exception var7) {
                return false;
            }
        } else {
            verifyKey = getValidatePublicKey(certId);
        }

        if (verifyKey == null) {
            log.error("未找到此序列号证书或证书校验失败。");
            return false;
        } else {
            try {
                stringSign = (String) data.get("sign");
                if (SDKUtil.isEmpty(stringSign)) {
                    log.error("签名串为空 验签失败");
                    return false;
                } else {
                    log.info("签名原文：[" + stringSign + "]");
                    String stringData = getSignStr(data);
                    log.info("待验签串：[" + stringData + "]");
                    boolean result = verifySm3WithSm2(stringData.getBytes("utf-8"), certId.getBytes("utf-8"), Base64.decodeBase64(stringSign), verifyKey);
                    return result;
                }
            } catch (Exception var6) {
                log.error(var6.getMessage(), var6);
                return false;
            }
        }
    }

    public static boolean verifySm3WithSm2(byte[] msg, byte[] userId, byte[] rs, PublicKey publicKey) {
        if (rs != null && msg != null && userId != null) {
            return rs.length != 64 ? false : verifySm3WithSm2Asn1Rs(msg, userId, rsPlainByteArrayToAsn1(rs), publicKey);
        } else {
            return false;
        }
    }

    public static boolean verifySm3WithSm2Asn1Rs(byte[] msg, byte[] userId, byte[] rs, PublicKey publicKey) {
        try {
            SM2ParameterSpec parameterSpec = new SM2ParameterSpec(userId);
            Signature verifier = Signature.getInstance("SM3withSM2", "BC");
            verifier.setParameter(parameterSpec);
            verifier.initVerify(publicKey);
            verifier.update(msg, 0, msg.length);
            return verifier.verify(rs);
        } catch (Exception var6) {
            return false;
        }
    }

    private static byte[] rsPlainByteArrayToAsn1(byte[] sign) {
        if (sign.length != 64) {
            throw new RuntimeException("err rs. ");
        } else {
            BigInteger r = new BigInteger(1, Arrays.copyOfRange(sign, 0, 32));
            BigInteger s = new BigInteger(1, Arrays.copyOfRange(sign, 32, 64));
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(new ASN1Integer(r));
            v.add(new ASN1Integer(s));

            try {
                return (new DERSequence(v)).getEncoded("DER");
            } catch (IOException var5) {
                throw new RuntimeException(var5);
            }
        }
    }

    public static X509Certificate verifyAndGetVerifyPubKey(String x509CertString) {
        if (SDKUtil.isEmpty(x509CertString)) {
            log.error("验签公钥证书传了空。");
            return null;
        } else if (verifyCerts510.containsKey(x509CertString)) {
            return (X509Certificate) verifyCerts510.get(x509CertString);
        } else {
            log.debug("验签公钥证书：[" + x509CertString + "]");
            X509Certificate x509Cert = CertUtil.genCertificateByStr(x509CertString);
            if (x509Cert == null) {
                log.error("convert signPubKeyCert failed");
                return null;
            } else {
                verifyCerts510.put(x509CertString, x509Cert);
                return x509Cert;
            }
        }
    }

    public static PublicKey getValidatePublicKey(String certId) {
        return getVerifySignKey(certId).getPubKey();
    }

    private static MerKey getVerifySignKey(String certId) {
        if (certId == null) {
            throw new IllegalArgumentException("null argument");
        } else {
//            if (!verifyCertsSnMap.containsKey(certId)) {
//                initVerifySignCerts();
//            }

            if (verifyCertsSnMap.containsKey(certId)) {
                return (MerKey) verifyCertsSnMap.get(certId);
            } else {
                log.error("cannot find this cert: " + certId);
                return null;
            }
        }
    }

//    private static void initVerifySignCerts() {
//        String dir = GmSDKConfig.getConfig().getValidateCertDir();
//        ConcurrentHashMap<String, MerKey> tmpVerifyCerts = new ConcurrentHashMap();
//        log.info("加载验证签名证书目录==>" + dir);
//        if (dir != null && dir.trim().length() != 0) {
//            CertificateFactory cf = null;
//            FileInputStream in = null;
//
//            try {
//                cf = CertificateFactory.getInstance("X.509", "BC");
//            } catch (Exception var27) {
//                log.error("LoadVerifyCert Error", var27);
//                return;
//            }
//
//            File fileDir = new File(dir);
//            File[] files = fileDir.listFiles(new GmCertUtil.CerFilter());
//
//            for(int i = 0; i < files.length; ++i) {
//                File file = files[i];
//
//                try {
//                    in = new FileInputStream(file.getAbsolutePath());
//                    X509Certificate cert = (X509Certificate)cf.generateCertificate(in);
//                    if (cert == null) {
//                        log.error("Load verify cert error, " + file.getAbsolutePath() + " has error cert content.");
//                    } else {
//                        MerKey key = new MerKey();
//                        key.setCertId(cert.getSerialNumber().toString(10));
//                        key.setPubKey(cert.getPublicKey());
//                        tmpVerifyCerts.put(key.getCertId(), key);
//                        log.info("[" + file.getAbsolutePath() + "][CertId=" + key.getCertId() + "]");
//                    }
//                } catch (CertificateException var24) {
//                    log.error("LoadVerifyCert Error CertificateException", var24);
//                } catch (FileNotFoundException var25) {
//                    log.error("LoadVerifyCert Error File Not Found", var25);
//                } catch (Exception var26) {
//                    log.error("LoadVerifyCert Error", var26);
//                } finally {
//                    if (null != in) {
//                        try {
//                            in.close();
//                        } catch (IOException var23) {
//                            log.error(var23.toString());
//                        }
//                    }
//
//                }
//            }
//
//            if (tmpVerifyCerts != null && tmpVerifyCerts.size() > 0) {
//                verifyCertsSnMap = tmpVerifyCerts;
//            }
//
//        } else {
//            log.error("WARN: uassdk.validateCert.dir is empty");
//        }
//    }


    static class MerKey {
        private String certId;
        private PublicKey pubKey;
        private PrivateKey priKey;

        MerKey() {
        }

        public String getCertId() {
            return this.certId;
        }

        public PublicKey getPubKey() {
            return this.pubKey;
        }

        public PrivateKey getPriKey() {
            return this.priKey;
        }

        public void setCertId(String certId) {
            this.certId = certId;
        }

        public void setPubKey(PublicKey pubKey) {
            this.pubKey = pubKey;
        }

        public void setPriKey(PrivateKey priKey) {
            this.priKey = priKey;
        }

        public String toString() {
            return "key: certId=" + this.certId;
        }
    }

    public static class Sm2Cert {
        private PrivateKey privateKey;
        private PublicKey publicKey;
        private String certId;

        public Sm2Cert() {
        }

        public PrivateKey getPrivateKey() {
            return this.privateKey;
        }

        public void setPrivateKey(PrivateKey privateKey) {
            this.privateKey = privateKey;
        }

        public PublicKey getPublicKey() {
            return this.publicKey;
        }

        public void setPublicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
        }

        public String getCertId() {
            return this.certId;
        }

        public void setCertId(String certId) {
            this.certId = certId;
        }
    }

}
