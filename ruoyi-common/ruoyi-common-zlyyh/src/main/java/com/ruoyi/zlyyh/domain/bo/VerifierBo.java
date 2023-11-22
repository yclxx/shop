package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 核销人员业务对象
 *
 * @author yzg
 * @date 2023-10-31
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class VerifierBo extends BaseEntity {

    @NotNull(message = "不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 平台标识
     */
    @NotNull(message = "平台标识不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long platformKey;

    /**
     * 名称
     */
    //@NotBlank(message = "名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String username;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空", groups = {AddGroup.class, EditGroup.class})
    private String mobile;

    /**
     * 状态 0 正常 1停用
     */
    @NotBlank(message = "状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private String status;

    /**
     * 人员类型，admin管理员 verifier 核销人员
     */
    @NotBlank(message = "人员类型，admin管理员 verifier 核销人员不能为空", groups = {AddGroup.class, EditGroup.class})
    private String verifierType;

    /**
     * 第三方平台联登唯一标识
     */
    private String openId;

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
    /**
     * 服务商表id
     */
    private Long extensionServiceProviderId;
    /**
     * 是否BD
     */
    private Boolean isBd;
    /**
     * 是否管理员
     */
    private Boolean isAdmin;
    /**
     * 是否核销人员
     */
    private Boolean isVerifier;
    /**
     * 所处地处
     */
    private String cityCode;
    /**
     * 归属公司
     */
    private String org;
}
