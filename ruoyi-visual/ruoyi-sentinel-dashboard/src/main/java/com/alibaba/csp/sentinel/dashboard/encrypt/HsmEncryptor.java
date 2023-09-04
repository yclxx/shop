package com.alibaba.csp.sentinel.dashboard.encrypt;

import cn.tass.exceptions.TAException;
import cn.tass.hsmApi.hsmGeneralFinance.hsmGeneralFinance;
import cn.tass.kits.Forms;
import com.alibaba.nacos.api.utils.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 银联加密机
 *
 * @author 25487
 */
public class HsmEncryptor {
    private static HsmEncryptor hsmEncryptor = null;
    private static hsmGeneralFinance hsm = null;
    private static EncryptConfig properties = null;

    private HsmEncryptor() {
    }

    private static void setHsm(hsmGeneralFinance hsm) {
        HsmEncryptor.hsm = hsm;
    }

    /**
     * 静态工厂方法
     *
     * @return 当前实例
     */
    public static HsmEncryptor getInstance(EncryptConfig properties) {
        if (hsmEncryptor == null || null == HsmEncryptor.properties) {
            HsmEncryptor.properties = properties;
            hsmEncryptor = new HsmEncryptor();
            if (StringUtils.isBlank(properties.getHsmIp())) {
                throw new IllegalArgumentException("缺少加密机IP");
            }
            if (StringUtils.isBlank(properties.getHsmPort())) {
                throw new IllegalArgumentException("缺少加密机端口");
            }
            if (StringUtils.isBlank(properties.getKeyType())) {
                throw new IllegalArgumentException("缺少加密机密钥类型");
            }
            if (null == properties.getKey()) {
                throw new IllegalArgumentException("缺少加密机密钥索引");
            }
            String linkNum = "-10";
            if (!StringUtils.isBlank(properties.getLinkNum())) {
                linkNum = properties.getLinkNum();
            }
            StringBuilder hmsSetting;
            if (properties.getHsmIp().contains(",")) {
                hmsSetting = new StringBuilder("{[LOGGER];logsw=error;logPath=./log/;");
                String[] split = properties.getHsmIp().split(",");
                for (int i = 0; i < split.length; i++) {
                    String s = split[i];
                    if (s.contains(":")) {
                        String[] ipPort = s.split(":");
                        hmsSetting.append("[HOST ").append(i + 1).append("];hsmModel=EHSM;host=").append(ipPort[0]).append(";linkNum=" + linkNum + ";timeout=3;port=").append(ipPort[1]).append(";");
                    } else {
                        hmsSetting.append("[HOST ").append(i + 1).append("];hsmModel=EHSM;host=").append(s).append(";linkNum=" + linkNum + ";timeout=3;port=").append(properties.getHsmPort()).append(";");
                    }
                }
                hmsSetting.append("}");
            } else {
                hmsSetting = new StringBuilder("{[LOGGER];logsw=error;logPath=./log/;[HOST 1];hsmModel=EHSM;host=" + properties.getHsmIp() + ";linkNum=" + linkNum + ";timeout=3;port=" + properties.getHsmPort() + ";}");
            }
            try {
                System.out.println("加密机连接配置：" + hmsSetting);
                setHsm(hsmGeneralFinance.getInstance(hmsSetting.toString()));
            } catch (TAException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return hsmEncryptor;
    }

    /**
     * 加密
     *
     * @param value 待加密字符串
     */
    public String encrypt(String value) {
        if (StringUtils.isBlank(value) || "null".equals(value)) {
            return value;
        }
        byte[] bytes;
        try {
            bytes = HsmEncryptor.hsm.generalDataEnc(0, properties.getKeyType(), properties.getKey(), "", 0, "",
                4, value.getBytes(StandardCharsets.UTF_8), "");
        } catch (TAException e) {
            throw new IllegalArgumentException(e);
        }
        return Forms.byteToHexString(bytes);
    }

    /**
     * 解密
     *
     * @param value 待加密字符串
     */
    public String decrypt(String value) {
        if (StringUtils.isBlank(value) || "null".equals(value)) {
            return value;
        }
        byte[] bytes;
        try {
            bytes = HsmEncryptor.hsm.generalDataDec(0, properties.getKeyType(), properties.getKey(), "", 0, "", 4, Forms.hexStringToByte(value), "");
        } catch (TAException e) {
            throw new IllegalArgumentException(e);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
