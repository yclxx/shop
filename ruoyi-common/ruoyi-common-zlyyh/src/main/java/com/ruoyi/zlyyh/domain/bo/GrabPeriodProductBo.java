package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 秒杀商品配置业务对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class GrabPeriodProductBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 秒杀配置ID
     */
    @NotNull(message = "秒杀配置ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long grabPeriodId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 排序：从小到大
     */
    private Long sort;

    private String productIds;


}
