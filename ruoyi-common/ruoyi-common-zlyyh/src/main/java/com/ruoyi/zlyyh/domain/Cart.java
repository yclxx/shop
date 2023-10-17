package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.math.BigDecimal;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 购物车对象 t_cart
 *
 * @author yzg
 * @date 2023-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_cart")
public class Cart extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 加入时售价
     */
    private BigDecimal createSellingPrice;
    /**
     * 数量
     */
    private Long quantity;

}
