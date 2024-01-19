package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 商户门店类别业务对象
 *
 * @author yzg
 * @date 2024-01-04
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantTypeBo extends BaseEntity {

    /**
     * 商户类别id
     */
    @NotNull(message = "商户类别id不能为空", groups = { EditGroup.class })
    private Long merchantTypeId;

    /**
     * 类别名称
     */
    @NotBlank(message = "类别名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String typeName;

    /**
     * 状态  0-正常  1-停用
     */
    @NotBlank(message = "状态  0-正常  1-停用不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;


}
