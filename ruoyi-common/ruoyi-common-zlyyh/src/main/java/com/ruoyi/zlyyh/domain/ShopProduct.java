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
 * 商品门店关联对象 t_shop_product
 *
 * @author yzg
 * @date 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop_product")
public class ShopProduct extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 门店ID
     */
    private Long shopId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 排序：从小到大
     */
    private Long sort;

}
