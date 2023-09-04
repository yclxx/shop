package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 门店组配置对象 t_shop_group
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop_group")
public class ShopGroup extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "shop_group_id")
    private Long shopGroupId;
    /**
     * 门店组名称
     */
    private String shopGroupName;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 平台标识
     */
    private Long platformKey;

}
