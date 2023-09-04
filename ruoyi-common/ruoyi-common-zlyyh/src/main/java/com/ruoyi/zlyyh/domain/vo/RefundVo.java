package com.ruoyi.zlyyh.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 退款订单登记视图对象
 *
 * @author yzg
 * @date 2023-08-07
 */
@Data
@ExcelIgnoreUnannotated
public class RefundVo {

    private static final long serialVersionUID = 1L;

    /**
     * 退款订单ID
     */
    @ExcelProperty(value = "退款订单ID")
    private Long refundId;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private Long number;

    /**
     * 退款金额
     */
    @ExcelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    /**
     * 退款申请人
     */
    @ExcelProperty(value = "退款申请人")
    private String refundApplicant;

    /**
     * 退款审核人
     */
    @ExcelProperty(value = "退款审核人")
    private String refundReviewer;

    /**
     * 0=审核中，1=审核通过，2=审核不通过
     */
    @ExcelProperty(value = "0=审核中，1=审核通过，2=审核不通过")
    private String status;

    /**
     * 审核拒绝原因
     */
    @ExcelProperty(value = "审核拒绝原因")
    private String refuseReason;

    /**
     * 退款原因
     */
    @ExcelProperty(value = "退款原因")
    private String refundRemark;


}
