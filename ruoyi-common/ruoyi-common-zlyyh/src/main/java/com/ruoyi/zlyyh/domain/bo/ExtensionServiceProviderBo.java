package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 商户拓展服务商业务对象
 *
 * @author yzg
 * @date 2023-09-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ExtensionServiceProviderBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 服务商名称
     */
    @NotBlank(message = "服务商名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String providerName;

    /**
     * 服务商联系人
     */
    private String providerUserName;

    /**
     * 联系电话
     */
    private String providerUserMobile;

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态（0正常 1停用）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;


}
