package com.ruoyi.system.api.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.encrypt.annotation.EncryptField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户信息对象 t_user
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 头像
     */
    private String userImg;
    /**
     * 手机号
     */
    @EncryptField()
    private String mobile;
    /**
     * openId，第三方平台联登唯一标识
     */
    private String openId;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 信息授权（0需要 1不需要）
     */
    private String reloadUser;
    /**
     * 权益会员（0未开通 1已开通 2已过期）
     */
    private String vipUser;
    /**
     * 首次访问所在城市
     */
    private String registerCityName;
    /**
     * 首次访问所在城市行政区号
     */
    private String registerCityCode;
    /**
     * 小程序关注状态（0-未知 1-已关注，2-取消关注）
     */
    private String followStatus;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;
    /**
     * 最后登录IP
     */
    private String loginIp;
    /**
     * 最后登录时间
     */
    private Date loginDate;
    /**
     * 最后登录所在城市
     */
    private String loginCityName;
    /**
     * 最后登录所在城市行政区号
     */
    private String loginCityCode;
    /**
     * 平台标识
     */
    private Long platformKey;

    /**
     * 权益会员失效时间
     */
    private Date vipExpiryDate;

    /**
     * 上次登录时间
     */
    private Date lastLoginDate;

    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;

    public User(){

    }

    public User(String mobile){
        this.setMobile(mobile);
    }
}
