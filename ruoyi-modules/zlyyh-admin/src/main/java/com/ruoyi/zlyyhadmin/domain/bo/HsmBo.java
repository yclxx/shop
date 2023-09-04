package com.ruoyi.zlyyhadmin.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author 25487
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HsmBo extends BaseEntity {

    @NotBlank(message = "加密内容不能为空", groups = {AddGroup.class})
    private String str;
}
