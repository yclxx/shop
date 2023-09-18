package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 观影用户信息业务对象
 *
 * @author yzg
 * @date 2023-09-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class UserIdcardBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long userIdcardId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 证件类型0-身份证 1-护照 2-港澳台居民居住证 3-户口簿
     */
    @NotBlank(message = "证件类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cardType;

    /**
     * 证件号
     */
    @NotBlank(message = "证件号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String idCard;


}
