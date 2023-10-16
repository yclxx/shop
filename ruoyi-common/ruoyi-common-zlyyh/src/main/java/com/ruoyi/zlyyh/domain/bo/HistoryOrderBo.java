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
 * 历史订单业务对象
 *
 * @author yzg
 * @date 2023-08-01
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class HistoryOrderBo extends BaseEntity {

    /**
     * 订单号
     */
    @NotNull(message = "订单号不能为空", groups = { EditGroup.class })
    private Long number;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String productName;

    /**
     * 商品图片
     */
    private String productImg;

    /**
     * 领取方式：0-免费领取，1-付费领取，2-积点兑换
     */
    @NotBlank(message = "领取方式不能为空", groups = { AddGroup.class, EditGroup.class })
    private String pickupMethod;

    /**
     * 订单类型：0-票券订单，1-美食订单
     */
    @NotBlank(message = "订单类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderType;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单优惠金额
     */
    private BigDecimal reducedPrice;

    /**
     * 需支付金额（订单总金额-优惠金额）
     */
    private BigDecimal wantAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal outAmount;

    /**
     * 支付完成时间
     */
    private Date payTime;

    /**
     * 订单失效时间
     */
    private Date expireDate;

    /**
     * 购买数量
     */
    private Long count;

    /**
     * 订单状态 0-待付款 1-支付确认中 2-支付成功 3-已关闭 4-退款处理中,5-退款成功,6-退款失败
     */
    @NotBlank(message = "订单状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 发放账号
     */
    private String account;

    /**
     * 发放状态 0-未发放 1-发放中 2-发放成功 3-发放失败
     */
    @NotBlank(message = "发放状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sendStatus;

    /**
     * 发放时间
     */
    private Date sendTime;

    /**
     * 外部产品ID
     */
    private String externalProductId;

    /**
     * 供应商订单号
     */
    private String externalOrderNumber;

    /**
     * 取码(充值)订单号
     */
    private String pushNumber;

    /**
     * 失败原因
     */
    private String failReason;

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

    private Long payMerchant;

    private String beginStartDate;

    private String endStartDate;

    /**
     * 供应商退款状态 0-退款中 1-退款成功 2-退款失败
     *
     */
    private String cancelStatus;
    /**
     * 发放金额
     */
    private BigDecimal externalProductSendValue;

    /**
     * 父级订单号（券包的子订单，此字段需要有值）
     */
    private Long parentNumber;

    private String cusRefund;

    private String verificationStatus;


}
