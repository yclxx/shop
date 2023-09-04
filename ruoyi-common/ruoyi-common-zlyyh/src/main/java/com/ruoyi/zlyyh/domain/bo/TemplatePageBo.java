package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 落地页业务对象
 *
 * @author yzg
 * @date 2023-06-09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplatePageBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long templateId;

    /**
     * 落地页描述
     */
    @NotBlank(message = "落地页描述不能为空", groups = { AddGroup.class, EditGroup.class })
    private String templateName;

    /**
     * 显示标题
     */
    @NotBlank(message = "显示标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String showTitle;

    /**
     * 页面标题
     */
    private String pageTitle;


}
