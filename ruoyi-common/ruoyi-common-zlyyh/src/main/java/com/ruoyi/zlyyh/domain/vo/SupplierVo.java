package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;



/**
 * 供应商视图对象
 *
 * @author yzg
 * @date 2023-10-11
 */
@Data
@ExcelIgnoreUnannotated
public class SupplierVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long supplierId;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 供应商地址
     */
    @ExcelProperty(value = "供应商地址")
    private String supplierAddress;

    /**
     * 对公账户
     */
    @ExcelProperty(value = "对公账户")
    private String corporateAccount;

    /**
     * 联系人
     */
    @ExcelProperty(value = "联系人")
    private String linkman;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String mobile;

    /**
     * 签约日期
     */
    @ExcelProperty(value = "签约日期")
    private Date contractDate;

    /**
     * 发票类型
     */
    @ExcelProperty(value = "发票类型")
    private String invoiceType;

    /**
     * 是否预警
     */
    @ExcelProperty(value = "是否预警", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_show_hide")
    private String warning;

    /**
     * 预警金额
     */
    @ExcelProperty(value = "预警金额")
    private BigDecimal warningAmount;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 预警邮箱
     */
    @ExcelProperty(value = "预警邮箱")
    private String warningEmail;


}
