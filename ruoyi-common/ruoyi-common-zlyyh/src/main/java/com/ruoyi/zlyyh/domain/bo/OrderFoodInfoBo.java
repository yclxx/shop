package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.math.BigDecimal;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 美食套餐详细订单业务对象
 *
 * @author yzg
 * @date 2023-05-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderFoodInfoBo extends BaseEntity {

    /**
     * 订单号
     */
    @NotNull(message = "订单号不能为空", groups = { EditGroup.class })
    private Long number;

    /**
     * 第三方订单id
     */
    @NotBlank(message = "第三方订单id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String bizOrderId;

    /**
     * 下单人姓名
     */
    @NotBlank(message = "下单人姓名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String userName;

    /**
     * 核销码
     */
    @NotBlank(message = "核销码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ticketCode;

    /**
     * 凭证ID
     */
    @NotBlank(message = "凭证ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String voucherId;

    /**
     * 凭证状态 可用EFFECTIVE、已用USED、失效CANCELED
     */
    @NotBlank(message = "凭证状态 可用EFFECTIVE、已用USED、失效CANCELED不能为空", groups = { AddGroup.class, EditGroup.class })
    private String voucherStatus;

    /**
     * 凭证生效时间
     */
    @NotBlank(message = "凭证生效时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private String effectTime;

    /**
     * 凭证过期时间
     */
    @NotBlank(message = "凭证过期时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private String expireTime;

    /**
     * 总份数
     */
    @NotNull(message = "总份数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer totalAmount;

    /**
     * 已使用份数
     */
    @NotNull(message = "已使用份数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer usedAmount;

    /**
     * 已退款份数（售中、售后）
     */
    @NotNull(message = "已退款份数（售中、售后）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer refundAmount;

    /**
     * 订单状态。当前只有PAID一个状态
     */
    @NotBlank(message = "订单状态。当前只有PAID一个状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderStatus;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String productName;

    /**
     * 商品id
     */
    @NotBlank(message = "商品id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String itemId;

    /**
     * 活动价
     */
    @NotNull(message = "活动价不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal sellingPrice;

    /**
     * 原价
     */
    @NotNull(message = "原价不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal officialPrice;


}
