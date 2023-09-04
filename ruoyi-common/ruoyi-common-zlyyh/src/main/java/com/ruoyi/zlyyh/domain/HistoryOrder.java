package com.ruoyi.zlyyh.domain;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 历史订单对象 t_history_order
 *
 * @author yzg
 * @date 2023-08-01
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_history_order")
public class HistoryOrder extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 订单号
     */
    @TableId(value = "number")
    private Long number;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品图片
     */
    private String productImg;
    /**
     * 领取方式：0-免费领取，1-付费领取，2-积点兑换
     */
    private String pickupMethod;
    /**
     * 订单类型：0-票券订单，1-美食订单，等同于产品类型
     */
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
    private String status;
    /**
     * 是否支持客户退款0-不支持 1-支持
     */
    private String cusRefund;
    /**
     * 发放账号
     */
    private String account;
    /**
     * 发放状态 0-未发放 1-发放中 2-发放成功 3-发放失败
     */
    private String sendStatus;
    /**
     * 发放时间
     */
    private Date sendTime;
    /**
     * 供应商退款状态0-退款中 1-退款成功 2-退款失败
     */
    private String cancelStatus;
    /**
     * 外部产品ID
     */
    private String externalProductId;
    /**
     * 发放金额，（票券类奖品无需填写，红包填写发放的红包金额，积点填写发放的积点数量）
     */
    private BigDecimal externalProductSendValue;
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
    private Long platformKey;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;
    /**
     * 支付商户号
     */
    private Long payMerchant;
    /**
     * 父级订单号（券包的子订单，此字段需要有值）
     */
    private Long parentNumber;

    public static HistoryOrder getHistoryOrder(Order order) {
        HistoryOrder historyOrder = new HistoryOrder();
        BeanUtil.copyProperties(order,historyOrder);
        historyOrder.setCreateTime(order.getCreateTime());
        historyOrder.setUpdateTime(order.getUpdateTime());
        historyOrder.setCreateBy(order.getCreateBy());
        historyOrder.setUpdateBy(order.getUpdateBy());
        return historyOrder;
    }
}
