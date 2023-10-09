package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 标签业务对象
 *
 * @author yzg
 * @date 2023-10-09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TagsBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long tagsId;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tagsName;

    /**
     * 类型
     */
    @NotBlank(message = "类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tagsType;


}
