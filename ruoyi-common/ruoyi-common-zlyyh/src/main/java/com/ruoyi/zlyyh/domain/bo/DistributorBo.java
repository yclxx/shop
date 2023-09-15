package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 分销商信息业务对象
 *
 * @author yzg
 * @date 2023-09-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DistributorBo extends BaseEntity {

    /**
     * 分销商ID
     */
    private String distributorId;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 分销商名称
     */
    @NotBlank(message = "分销商名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String distributorName;

    /**
     * 通知地址
     */
    private String backUrl;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;
}
