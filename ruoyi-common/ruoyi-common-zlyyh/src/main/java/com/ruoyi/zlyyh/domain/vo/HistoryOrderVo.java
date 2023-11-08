package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.annotation.Sensitive;
import com.ruoyi.common.core.enums.SensitiveStrategy;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 历史订单视图对象
 *
 * @author yzg
 * @date 2023-08-01
 */
@Data
@ExcelIgnoreUnannotated
public class HistoryOrderVo {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private Long number;

    /**
     * 商品ID
     */
    @ExcelProperty(value = "商品ID")
    private Long productId;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    private String productName;

    /**
     * 商品图片
     */
    @ExcelProperty(value = "商品图片")
    private String productImg;

    /**
     * 领取方式：0-免费领取，1-付费领取，2-积点兑换
     */
    @ExcelProperty(value = "领取方式：0-免费领取，1-付费领取，2-积点兑换")
    private String pickupMethod;

    /**
     * 订单类型：0-票券订单，1-美食订单，等同于产品类型
     */
    @ExcelProperty(value = "订单类型：0-票券订单，1-美食订单，等同于产品类型")
    private String orderType;

    /**
     * 订单总金额
     */
    @ExcelProperty(value = "订单总金额")
    private BigDecimal totalAmount;

    /**
     * 订单优惠金额
     */
    @ExcelProperty(value = "订单优惠金额")
    private BigDecimal reducedPrice;

    /**
     * 需支付金额（订单总金额-优惠金额）
     */
    @ExcelProperty(value = "需支付金额", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "订=单总金额-优惠金额")
    private BigDecimal wantAmount;

    /**
     * 实际支付金额
     */
    @ExcelProperty(value = "实际支付金额")
    private BigDecimal outAmount;

    /**
     * 支付完成时间
     */
    @ExcelProperty(value = "支付完成时间")
    private Date payTime;

    /**
     * 订单失效时间
     */
    @ExcelProperty(value = "订单失效时间")
    private Date expireDate;

    /**
     * 购买数量
     */
    @ExcelProperty(value = "购买数量")
    private Long count;

    /**
     * 订单状态 0-待付款 1-支付确认中 2-支付成功 3-已关闭 4-退款处理中,5-退款成功,6-退款失败
     */
    @ExcelProperty(value = "订单状态 0-待付款 1-支付确认中 2-支付成功 3-已关闭 4-退款处理中,5-退款成功,6-退款失败")
    private String status;

    /**
     * 发放账号
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    @ExcelProperty(value = "发放账号")
    private String account;

    /**
     * 发放状态 0-未发放 1-发放中 2-发放成功 3-发放失败
     */
    @ExcelProperty(value = "发放状态 0-未发放 1-发放中 2-发放成功 3-发放失败 ")
    private String sendStatus;

    /**
     * 发放时间
     */
    @ExcelProperty(value = "发放时间")
    private Date sendTime;

    /**
     * 供应商退款状态0-退款中 1-退款成功 2-退款失败
     */
    @ExcelProperty(value = "供应商退款状态0-退款中 1-退款成功 2-退款失败")
    private String cancelStatus;

    /**
     * 外部产品ID
     */
    @ExcelProperty(value = "外部产品ID")
    private String externalProductId;

    /**
     * 发放金额，（票券类奖品无需填写，红包填写发放的红包金额，积点填写发放的积点数量）
     */
    @ExcelProperty(value = "发放金额，", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "票=券类奖品无需填写，红包填写发放的红包金额，积点填写发放的积点数量")
    private BigDecimal externalProductSendValue;

    /**
     * 供应商订单号
     */
    @ExcelProperty(value = "供应商订单号")
    private String externalOrderNumber;

    /**
     * 取码(充值)订单号
     */
    @ExcelProperty(value = "取码(充值)订单号")
    private String pushNumber;

    /**
     * 失败原因
     */
    @ExcelProperty(value = "失败原因")
    private String failReason;

    /**
     * 下单所在城市
     */
    @ExcelProperty(value = "下单所在城市")
    private String orderCityName;

    /**
     * 下单所在城市行政区号
     */
    @ExcelProperty(value = "下单所在城市行政区号")
    private String orderCityCode;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 支付商户号
     */
    @ExcelProperty(value = "支付商户号")
    private Long payMerchant;

    /**
     * 父级订单号（券包的子订单，此字段需要有值）
     */
    @ExcelProperty(value = "父级订单号", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "券=包的子订单，此字段需要有值")
    private Long parentNumber;

    private MerchantVo merchantVo;

    /**
     * 订单卡券
     */
    private List<OrderCardVo> orderCardVos;

    private OrderFoodInfoVo orderFoodInfoVo;

    private List<OrderUnionSendVo> orderUnionSendVos;

    private String cusRefund;

    private String verificationStatus;

    /**
     * 优惠券id
     */
    private Long couponId;
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

    /**
     * 银联分销：0-不通过，1-通过
     */
    private String unionPay;

    /**
     * 银联产品编号
     */
    private String unionProductId;
    /**
     * 场次ID
     */
    private Long productSessionId;
    /**
     * 规格ID
     */
    private Long productSkuId;
    /**
     * 场次名称
     */
    private String productSessionName;
    /**
     * 规格名称
     */
    private String productSkuName;


    /**
     * 大订单编号
     */
    private Long collectiveNumber;
    /**
     * 支持端
     */
    private String supportChannel;

}
