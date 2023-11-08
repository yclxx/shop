package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 核销人员对象 t_verifier
 *
 * @author yzg
 * @date 2023-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_verifier")
public class Verifier extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 状态 0 正常 1停用
     */
    private String status;
    /**
     * 人员类型，admin 管理员 verifier 核销人员
     */
    private String verifierType;
    /**
     * 第三方平台联登唯一标识
     */
    private String openId;
    /***
     * 上级id
     */
    private Long superiorId;
    /**
     * 最后登录IP
     */
    private String loginIp;
    /**
     * 最后登录时间
     */
    private Date loginDate;
    /**
     * 上次登录时间
     */
    private Date lastLoginDate;

    /**
     * 信息授权
     */
    private String reloadUser;
    /**
     * 部门id
     */
    private Long sysDeptId;
    /**
     * 用户id
     */
    private Long sysUserId;

    public Verifier() {
    }

    public Verifier(String mobile) {
        this.mobile = mobile;
    }
}
