package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 用户入群问题反馈业务对象
 *
 * @author yzg
 * @date 2024-02-22
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatformGroupProblemBo extends BaseEntity {

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
     * 用户编号
     */
    @NotNull(message = "用户编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;


}
