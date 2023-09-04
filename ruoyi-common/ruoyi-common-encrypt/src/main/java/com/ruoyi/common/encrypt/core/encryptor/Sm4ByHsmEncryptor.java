package com.ruoyi.common.encrypt.core.encryptor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.tass.exceptions.TAException;
import cn.tass.hsmApi.hsmGeneralFinance.hsmGeneralFinance;
import cn.tass.kits.Forms;
import com.ruoyi.common.encrypt.core.EncryptContext;
import com.ruoyi.common.encrypt.enumd.AlgorithmType;
import com.ruoyi.common.encrypt.enumd.EncodeType;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * sm4算法实现 基于银联加密机
 *
 * @author yzg
 * @version 1.0.0
 */
@Slf4j
public class Sm4ByHsmEncryptor extends AbstractEncryptor {

    private final hsmGeneralFinance hsm;
    private final String keyType;
    private final Integer key;

    public Sm4ByHsmEncryptor(EncryptContext context) {
        super(context);
        log.info("开始连接加密机");
        TimeInterval timer = DateUtil.timer();
        if (StrUtil.isBlank(context.getHsmIp())) {
            throw new IllegalArgumentException("缺少加密机IP");
        }
        if (StrUtil.isBlank(context.getHsmPort())) {
            throw new IllegalArgumentException("缺少加密机端口");
        }
        if (StrUtil.isBlank(context.getKeyType())) {
            throw new IllegalArgumentException("缺少加密机密钥类型");
        }
        if (null == context.getKey()) {
            throw new IllegalArgumentException("缺少加密机密钥索引");
        }
        this.keyType = context.getKeyType();
        this.key = context.getKey();
        StringBuilder hmsSetting;
        if (context.getHsmIp().contains(",")) {
            hmsSetting = new StringBuilder("{[LOGGER];logsw=error;logPath=./log/;");
            String[] split = context.getHsmIp().split(",");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.contains(":")) {
                    String[] ipPort = s.split(":");
                    hmsSetting.append("[HOST ").append(i + 1).append("];hsmModel=EHSM;host=").append(ipPort[0]).append(";linkNum=").append(context.getLinkNum()).append(";timeout=3;port=").append(ipPort[1]).append(";");
                } else {
                    hmsSetting.append("[HOST ").append(i + 1).append("];hsmModel=EHSM;host=").append(s).append(";linkNum=").append(context.getLinkNum()).append(";timeout=3;port=").append(context.getHsmPort()).append(";");
                }
            }
            hmsSetting.append("}");
        } else {
            hmsSetting = new StringBuilder("{[LOGGER];logsw=error;logPath=./log/;[HOST 1];hsmModel=EHSM;host=" + context.getHsmIp() + ";linkNum=" + context.getLinkNum() + ";timeout=3;port=" + context.getHsmPort() + ";}");
        }
        try {
            log.info("加密机连接配置参数：{}", hmsSetting);
            this.hsm = hsmGeneralFinance.getInstance(hmsSetting.toString());
        } catch (TAException e) {
            throw new IllegalArgumentException(e);
        }
        log.info("加密机连接完成耗时：{}毫秒", timer.interval());
    }

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.SM4HSM;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        if (StrUtil.isBlank(value) || "null".equals(value)) {
            return value;
        }
        byte[] bytes;
        try {
            bytes = this.hsm.generalDataEnc(0, keyType, key, "", 0, "",
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
    @Override
    public String decrypt(String value) {
        if (StrUtil.isBlank(value) || "null".equals(value)) {
            return value;
        }
        byte[] bytes;
        try {
            bytes = this.hsm.generalDataDec(0, keyType, key, "", 0, "", 4, Forms.hexStringToByte(value), "");
        } catch (TAException e) {
            throw new IllegalArgumentException(e);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
