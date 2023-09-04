package com.ruoyi.zlyyh.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 美食套餐详细订单视图对象
 *
 * @author yzg
 * @date 2023-05-15
 */
@Data
@ExcelIgnoreUnannotated
public class OrderFoodInfoVo {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private Long number;

    /**
     * 第三方订单id
     */
    @ExcelProperty(value = "第三方订单id")
    private String bizOrderId;

    /**
     * 下单人姓名
     */
    @ExcelProperty(value = "下单人姓名")
    private String userName;

    /**
     * 核销码
     */
    @ExcelProperty(value = "核销码")
    private String ticketCode;

    /**
     * 凭证ID
     */
    @ExcelProperty(value = "凭证ID")
    private String voucherId;

    /**
     * 凭证状态 可用EFFECTIVE、已用USED、失效CANCELED
     */
    @ExcelProperty(value = "凭证状态")
    private String voucherStatus;

    /**
     * 凭证生效时间
     */
    @ExcelProperty(value = "凭证生效时间")
    private String effectTime;

    /**
     * 凭证过期时间
     */
    @ExcelProperty(value = "凭证过期时间")
    private String expireTime;

    /**
     * 总份数
     */
    @ExcelProperty(value = "总份数")
    private Integer totalAmount;

    /**
     * 已使用份数
     */
    @ExcelProperty(value = "已使用份数")
    private Integer usedAmount;

    /**
     * 已退款份数（售中、售后）
     */
    @ExcelProperty(value = "已退款份数")
    private Integer refundAmount;

    /**
     * 订单状态。当前只有PAID一个状态
     */
    @ExcelProperty(value = "订单状态")
    private String orderStatus;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    private String productName;

    /**
     * 商品id
     */
    @ExcelProperty(value = "商品id")
    private String itemId;

    /**
     * 活动价
     */
    @ExcelProperty(value = "活动价")
    private BigDecimal sellingPrice;

    /**
     * 原价
     */
    @ExcelProperty(value = "原价")
    private BigDecimal officialPrice;


}
