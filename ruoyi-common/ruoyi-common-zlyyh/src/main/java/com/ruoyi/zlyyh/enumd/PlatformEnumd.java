package com.ruoyi.zlyyh.enumd;

import com.ruoyi.common.core.utils.StringUtils;
import lombok.Getter;

/**
 * 平台
 */
@Getter
public enum PlatformEnumd {
    /**
     * 微信小程序
     */
    MP_WX("mp-weixin"),
    /**
     * 云闪付小程序
     */
    MP_YSF("mp-union"),
    /**
     * H5
     */
    WEB("web");

    private String platformType;

    PlatformEnumd(String platformType) {
        this.platformType = platformType;
    }

    public static String getPlatformSupportChannel(PlatformEnumd platformEnumd) {
        if (MP_WX == platformEnumd) {
            return "1";
        } else {
            return "0";
        }
    }

    public static PlatformEnumd getPlatformEnumd(String str) {
        if (StringUtils.isBlank(str)) {
            return MP_YSF;
        }
        PlatformEnumd[] values = PlatformEnumd.values();
        for (PlatformEnumd value : values) {
            if (str.equals(value.getPlatformType())) {
                return value;
            }
        }
        return MP_YSF;
    }
}
