package com.ruoyi.zlyyh.properties.utils;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.properties.YsfProperties;
import com.ruoyi.zlyyh.service.YsfConfigService;
import lombok.extern.slf4j.Slf4j;

/**
 * 云闪付接口 配置属性
 *
 * @author Lion Li
 */
@Slf4j
public class YsfPropertiesUtils {
    private static YsfProperties YSF_PROPERTIES;
    private static YsfConfigService configService;

    static {
        try {
            YSF_PROPERTIES = SpringUtils.getBean(YsfProperties.class);
            configService = SpringUtils.getBean(YsfConfigService.class);
        } catch (Exception e) {
            log.error("加载云闪付配置信息失败");
        }
    }

    public static String getBackendTokenUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.backendTokenUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getBackendTokenUrl();
    }

    public static String getTokenUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.tokenUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getTokenUrl();
    }

    public static String getUserMobileUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.userMobileUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getUserMobileUrl();
    }

    public static String getSysId(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.sysId");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getSysId();
    }

    public static String getMemberVipBalanceUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.memberVipBalanceUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getMemberVipBalanceUrl();
    }

    public static String getMemberVipAcquireUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.memberVipAcquireUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getMemberVipAcquireUrl();
    }

    public static String getSendCouponUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.sendCouponUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getSendCouponUrl();
    }

    public static String getQueryCouponUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.queryCouponUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getQueryCouponUrl();
    }

    public static String getAcctEntityTp(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.acctEntityTp");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getAcctEntityTp();
    }

    public static String getMemberPointDeductUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.memberPointDeductUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getMemberPointDeductUrl();
    }

    public static String getMemberPointAcquireUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.memberPointAcquireUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getMemberPointAcquireUrl();
    }

    public static String getMemberPointBalanceUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.memberPointBalanceUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getMemberPointBalanceUrl();
    }

    public static String getMemberPointDeductSource(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.memberPointDeductSource");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getMemberPointDeductSource();
    }

    public static String getMemberPointAcquireSource(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.memberPointAcquireSource");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getMemberPointAcquireSource();
    }

    public static String getAppId(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.appId");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getAppId();
    }

    public static String getSecret(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.secret");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getSecret();
    }

    public static String getSymmetricKey(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.symmetricKey");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getSymmetricKey();
    }

    public static String getRsaPrivateKey(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.rsaPrivateKey");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getRsaPrivateKey();
    }

    public static String getSendAcquireUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.sendAcquireUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getSendAcquireUrl();
    }

    public static String getInsAcctId(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.insAcctId");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getInsAcctId();
    }

    public static String getBizTp(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.bizTp");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getBizTp();
    }

    public static String getSubBizTp(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.subBizTp");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getSubBizTp();
    }

    public static String getRegisterMissionUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.registerMissionUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getRegisterMissionUrl();
    }

    public static String getQueryMissionUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.queryMissionUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getQueryMissionUrl();
    }

    public static String getGetFrontToken(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysf.getFrontToken");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return YSF_PROPERTIES.getGetFrontToken();
    }
}
