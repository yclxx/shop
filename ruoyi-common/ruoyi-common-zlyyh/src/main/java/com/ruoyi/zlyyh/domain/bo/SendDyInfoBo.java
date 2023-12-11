package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户订阅业务对象
 *
 * @author yzg
 * @date 2023-12-07
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SendDyInfoBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 平台标识
     */
    @NotNull(message = "平台标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long platformKey;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 用户openId
     */
    @NotBlank(message = "用户openId不能为空", groups = { AddGroup.class, EditGroup.class })
    private String openId;

    /**
     * 订阅模板ID
     */
    @NotBlank(message = "订阅模板ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tmplId;

    /**
     * 状态
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 次数
     */
    @NotNull(message = "次数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dyCount;


}
