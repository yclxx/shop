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
 * 美食套餐详细订单对象 t_order_food_info
 *
 * @author yzg
 * @date 2023-05-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_food_info")
public class OrderFoodInfo extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 订单号
     */
    @TableId(value = "number")
    private Long number;
    /**
     * 第三方订单id
     */
    private String bizOrderId;
    /**
     * 下单人姓名
     */
    private String userName;
    /**
     * 核销码
     */
    private String ticketCode;
    /**
     * 凭证ID
     */
    private String voucherId;
    /**
     * 凭证状态 可用EFFECTIVE、已用USED、失效CANCELED
     */
    private String voucherStatus;
    /**
     * 凭证生效时间
     */
    private String effectTime;
    /**
     * 凭证过期时间
     */
    private String expireTime;
    /**
     * 总份数
     */
    private Integer totalAmount;
    /**
     * 已使用份数
     */
    private Integer usedAmount;
    /**
     * 已退款份数（售中、售后）
     */
    private Integer refundAmount;
    /**
     * 订单状态。当前只有PAID一个状态
     */
    private String orderStatus;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品id
     */
    private String itemId;
    /**
     * 活动价
     */
    private BigDecimal sellingPrice;
    /**
     * 原价
     */
    private BigDecimal officialPrice;

}
