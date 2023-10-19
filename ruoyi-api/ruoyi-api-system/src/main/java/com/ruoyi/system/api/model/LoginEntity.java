package com.ruoyi.system.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录返回结果定义
 */
@Data
public class LoginEntity implements Serializable {
    /**
     * 平台key
     */
    private Long platformKey;
    /**
     * 用户手机号
     */
    private String mobile;
    /**
     * 用户openId
     */
    private String openId;
}
