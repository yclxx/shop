package com.ruoyi.zlyyhmobile.domain.bo;

import lombok.Data;

/**
 * 用户操作记录参数
 */
@Data
public class UserRecordLog {
    /**
     * openId
     */
    private String openId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 来源
     */
    private String source;
    /**
     * 客户端
     */
    private String supportChannel;
}
