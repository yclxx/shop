package com.ruoyi.system.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 小程序登录用户身份权限
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class XcxLoginUser extends LoginUser {

    private static final long serialVersionUID = 1L;

    /**
     * openid
     */
    private String openid;

    /**
     * 城市
     */
    private String cityName;

    /**
     * 城市 行政区号
     */
    private String cityCode;

    /**
     * 信息授权（0需要 1不需要）
     */
    private String reloadUser;

    /**
     * 用户创建时间
     */
    private Date createTime;

    /**
     * 渠道用户ID
     */
    private Long userChannelId;

}
