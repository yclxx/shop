package com.ruoyi.zlyyh.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 分销用户账户视图对象
 *
 * @author yzg
 * @date 2023-11-09
 */
@Data
@ExcelIgnoreUnannotated
public class ShareUserAccountVo {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 是否可提现：0-可提现，1-禁止提现
     */
    @ExcelProperty(value = "是否可提现：0-可提现，1-禁止提现")
    private String status;

    /**
     * 冻结金额
     */
    @ExcelProperty(value = "冻结金额")
    private BigDecimal freezeBalance;

    /**
     * 可提金额
     */
    @ExcelProperty(value = "可提金额")
    private BigDecimal balance;

    /**
     * 已提金额
     */
    @ExcelProperty(value = "已提金额")
    private BigDecimal withdrawDeposit;

    /**
     * 已退金额
     */
    @ExcelProperty(value = "已退金额")
    private BigDecimal refundBalance;


}
