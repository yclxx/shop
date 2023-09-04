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
 * 权益包商品对象 t_equity_product
 *
 * @author yzg
 * @date 2023-06-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_equity_product")
public class EquityProduct extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 权益包ID
     */
    private Long equityId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 商品价值
     */
    private BigDecimal productAmount;
    /**
     * 产品归属
     */
    private String equityType;
    /**
     * 可领数量
     */
    private Long sendCount;
    /**
     * 排序
     */
    private Long sort;
    /**
     * 状态
     */
    private String status;

}
