package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.encrypt.annotation.EncryptField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单对象 t_order
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 订单号
     */
    @TableId(value = "number",type = IdType.INPUT)
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
     * 订单类型：0-票券订单，1-美食订单
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
    @EncryptField()
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
    private Long platformKey;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 支付商戶
     */
    private Long payMerchant;

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

    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;

    /**
     * 可使用开始时间
     */
    private Date usedStartTime;

    /**
     * 可使用结束时间
     */
    private Date usedEndTime;

    /**
     * 使用时间
     */
    private Date usedTime;
}
