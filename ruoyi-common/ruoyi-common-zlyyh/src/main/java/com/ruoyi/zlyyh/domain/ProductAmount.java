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
 * 商品价格配置对象 t_product_amount
 *
 * @author yzg
 * @date 2023-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product_amount")
public class ProductAmount extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "amount_id")
    private Long amountId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 发放金额，（票券类奖品无需填写，红包填写发放的红包金额，积点填写发放的积点数量）
     */
    private BigDecimal externalProductSendValue;
    /**
     * 中奖概率
     */
    private BigDecimal drawProbability;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
