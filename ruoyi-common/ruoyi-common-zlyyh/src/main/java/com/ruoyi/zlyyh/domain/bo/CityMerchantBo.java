package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 城市商户业务对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */

@Data
public class CityMerchantBo{

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
     * 行政编码
     */
    @NotBlank(message = "行政编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String adcode;

    /**
     * 行政区名称
     */
    private String areaName;

    /**
     * 商户ID
     */
    @NotNull(message = "商户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long merchantId;


}
