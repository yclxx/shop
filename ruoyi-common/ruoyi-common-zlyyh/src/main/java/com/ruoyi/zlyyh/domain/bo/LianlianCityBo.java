package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 联联市级城市业务对象
 *
 * @author yzg
 * @date 2023-09-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class LianlianCityBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long cityId;

    /**
     * 城市名称
     */
    @NotBlank(message = "城市名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cityName;

    /**
     * 城市区号
     */
    @NotBlank(message = "城市区号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cityCode;

    /**
     * 状态：0-可用，1-禁用
     */
    @NotBlank(message = "状态：0-可用，1-禁用不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;


}
