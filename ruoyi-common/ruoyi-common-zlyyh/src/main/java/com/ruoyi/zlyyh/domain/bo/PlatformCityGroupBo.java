package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 平台城市企业微信群业务对象
 *
 * @author yzg
 * @date 2024-02-21
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatformCityGroupBo extends BaseEntity {

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
     * 城市名称
     */
    @NotBlank(message = "城市名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cityName;

    /**
     * 城市编码
     */
    @NotBlank(message = "城市编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cityCode;

    /**
     * 企业微信群码
     */
    @NotBlank(message = "企业微信群码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String groupImages;

    /**
     * 状态
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;


}
