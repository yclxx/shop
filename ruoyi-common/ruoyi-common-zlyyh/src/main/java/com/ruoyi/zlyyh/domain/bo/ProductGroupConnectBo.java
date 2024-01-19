package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 商品商品组关联业务对象
 *
 * @author yzg
 * @date 2024-01-16
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductGroupConnectBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 商品组ID
     */
    @NotNull(message = "商品组ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productGroupId;

    /**
     * 商品Id
     */
    @NotNull(message = "商品Id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    private Integer type;

    private List<Long> productIds;


}
