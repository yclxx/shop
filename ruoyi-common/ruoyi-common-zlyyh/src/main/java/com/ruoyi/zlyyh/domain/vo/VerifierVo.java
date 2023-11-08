package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 核销人员视图对象
 *
 * @author yzg
 * @date 2023-10-31
 */
@Data
@ExcelIgnoreUnannotated
public class VerifierVo {
    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String mobile;

    /**
     * 状态 0 正常 1停用
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 人员类型，admin管理员 verifier 核销人员
     */
    @ExcelProperty(value = "人员类型，admin管理员 verifier 核销人员")
    private String verifierType;

    /**
     * 第三方平台联登唯一标识
     */
    @ExcelProperty(value = "第三方平台联登唯一标识")
    private String openId;

    /***
     * 上级id
     */
    @ExcelProperty(value = "上级id")
    private Long superiorId;

    /**
     * 最后登录IP
     */
    @ExcelProperty(value = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @ExcelProperty(value = "最后登录时间")
    private Date loginDate;
    /**
     * 上次登录时间
     */
    @ExcelProperty(value = "上次登录时间")
    private Date lastLoginDate;

    /**
     * 信息授权
     */
    private String reloadUser;

    /**
     * 部门id
     */
    @ExcelProperty(value = "部门id")
    private Long sysDeptId;

    /**
     * 用户id
     */
    @ExcelProperty(value = "用户id")
    private Long sysUserId;
}
