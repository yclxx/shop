package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 大订单业务对象
 *
 * @author yzg
 * @date 2023-10-16
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CollectiveOrderBo extends BaseEntity {

    /**
     * 大订单号
     */
    @NotNull(message = "大订单号不能为空", groups = { EditGroup.class })
    private Long collectiveNumber;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal totalAmount;

    /**
     * 订单优惠金额
     */
    @NotNull(message = "订单优惠金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal reducedPrice;

    /**
     * 需支付金额（订单总金额-优惠金额）
     */
    @NotNull(message = "需支付金额（订单总金额-优惠金额）不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal wantAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal outAmount;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 支付完成时间
     */
    private Date payTime;

    /**
     * 订单失效时间
     */
    private Date expireDate;

    /**
     * 订单状态 0-待付款 1-支付确认中 2-支付成功 3-已关闭 4-退款处理中,5-退款成功,6-退款失败
     */
    @NotBlank(message = "订单状态 0-待付款 1-支付确认中 2-支付成功 3-已关闭 4-退款处理中,5-退款成功,6-退款失败不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 退款状态0-退款中 1-退款成功 2-退款失败 3-部分退款成功
     */
    private String cancelStatus;
    /**
     * 已退款金额
     */
    private BigDecimal cancelAmount;

    /**
     * 下单所在城市
     */
    private String orderCityName;

    /**
     * 下单所在城市行政区号
     */
    private String orderCityCode;

    /**
     * 平台标识
     */
    @NotNull(message = "平台标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long platformKey;

    /**
     * 支付商户号
     */
    @NotNull(message = "支付商户号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long payMerchant;

    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;


}
