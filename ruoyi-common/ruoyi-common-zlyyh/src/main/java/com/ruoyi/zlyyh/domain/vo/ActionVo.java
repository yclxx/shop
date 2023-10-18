package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;



/**
 * 优惠券批次视图对象
 *
 * @author yzg
 * @date 2023-10-12
 */
@Data
@ExcelIgnoreUnannotated
public class ActionVo {

    private static final long serialVersionUID = 1L;

    /**
     * 批次ID
     */
    @ExcelProperty(value = "批次ID")
    private Long actionId;

    /**
     * 批次号
     */
    @ExcelProperty(value = "批次号")
    private String actionNo;

    /**
     * 批次描述
     */
    @ExcelProperty(value = "批次描述")
    private String actionNote;

    /**
     * 优惠券名称
     */
    @ExcelProperty(value = "优惠券名称")
    private String couponName;

    /**
     * 优惠金额
     */
    @ExcelProperty(value = "优惠金额")
    private BigDecimal couponAmount;

    /**
     * 最低消费金额
     */
    @ExcelProperty(value = "最低消费金额")
    private BigDecimal minAmount;

    /**
     * 优惠券类型
     */
    @ExcelProperty(value = "优惠券类型")
    private String couponType;

    /**
     * 优惠券可使用起始日期
     */
    @ExcelProperty(value = "优惠券可使用起始日期")
    private Date periodOfStart;

    /**
     * 使用有效截止日期
     */
    @ExcelProperty(value = "使用有效截止日期")
    private Date periodOfValidity;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 优惠券数量
     */
    @ExcelProperty(value = "优惠券数量")
    private Long couponCount;

    /**
     * 优惠券描述
     */
    @ExcelProperty(value = "优惠券描述")
    private String couponDescription;

    /**
     * 可兑换起始日期
     */
    @ExcelProperty(value = "可兑换起始日期")
    private Date conversionStartDate;

    /**
     * 可兑换截止日期
     */
    @ExcelProperty(value = "可兑换截止日期")
    private Date conversionEndDate;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 优惠券图片
     */
    private String couponImage;


}
