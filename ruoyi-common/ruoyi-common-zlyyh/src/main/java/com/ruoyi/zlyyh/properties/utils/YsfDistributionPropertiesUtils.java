package com.ruoyi.zlyyh.properties.utils;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.properties.YsfDistributionProperties;
import com.ruoyi.zlyyh.service.YsfConfigService;
import lombok.extern.slf4j.Slf4j;

/**
 * 云闪付内容分销（渠道方） 配置书香
 * @author hyh
 */
@Slf4j
public class YsfDistributionPropertiesUtils {

    private static YsfDistributionProperties ysfDistributionProperties;
    private static YsfConfigService configService;

    static {
        try {
            ysfDistributionProperties = SpringUtils.getBean(YsfDistributionProperties.class);
            configService = SpringUtils.getBean(YsfConfigService.class);
        } catch (Exception e) {
            log.error("加载云闪付配置信息失败");
        }
    }

    public static String getJDAppId(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysfdistribution.JDAppId");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return ysfDistributionProperties.getJDAppId();
    }

    public static String getJCAppId(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysfdistribution.JCAppId");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return ysfDistributionProperties.getJCAppId();
    }

    public static String getUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysfdistribution.url");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return ysfDistributionProperties.getUrl();
    }

    public static String getCertPwd(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysfdistribution.certPwd");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return ysfDistributionProperties.getCertPwd();
    }

    public static String getCertPathJD(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysfdistribution.certPathJD");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return ysfDistributionProperties.getCertPathJD();
    }

    public static String getCertPathJC(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysfdistribution.certPathJC");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return ysfDistributionProperties.getCertPathJC();
    }

    public static String getBackUrl(Long platformKey) {
        String value = configService.queryValueByKey(platformKey, "ysfdistribution.backUrl");
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return ysfDistributionProperties.getBackUrl();
    }
}
