package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 品牌管理业务对象
 *
 * @author yzg
 * @date 2023-12-29
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class BrandBo extends BaseEntity {

    /**
     * 品牌ID
     */
    @NotNull(message = "品牌ID不能为空", groups = { EditGroup.class })
    private Long brandId;

    /**
     * 品牌名称
     */
    @NotBlank(message = "品牌名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String brandName;

    /**
     * 品牌logo
     */
    private String brandImg;

    /**
     * 状态
     */
    private String status;

    /**
     * 排序
     */
    private Long sort;


}
