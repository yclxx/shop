package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 落地页配置业务对象
 *
 * @author yzg
 * @date 2023-06-09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateSettingBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 落地页
     */
    @NotNull(message = "落地页不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long templateId;

    /**
     * 图片
     */
    @NotBlank(message = "图片不能为空", groups = { AddGroup.class, EditGroup.class })
    private String img;

    /**
     * 是否按钮
     */
    @NotBlank(message = "是否按钮不能为空", groups = { AddGroup.class, EditGroup.class })
    private String btn;

    /**
     * 跳转类型
     */
    private String toType;

    /**
     * 小程序ID
     */
    private String appId;

    /**
     * 页面地址
     */
    private String url;

    /**
     * 排序
     */
    private Long sort;


}
