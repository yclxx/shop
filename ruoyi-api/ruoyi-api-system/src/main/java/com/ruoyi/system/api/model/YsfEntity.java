package com.ruoyi.system.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 25487
 */
@Data
public class YsfEntity implements Serializable {
    /** openId */
    private String openId;
    /** accessToken */
    private String accessToken;
    /** 基础服务令牌 backendToken */
    private String backendToken;
    /** 平台key */
    private Long platformKey;
    /** 用户手机号 */
    private String mobile;
}
