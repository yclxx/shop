package com.ruoyi.zlyyh.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 分销记录视图对象
 *
 * @author yzg
 * @date 2023-11-09
 */
@Data
@ExcelIgnoreUnannotated
public class ShareUserRecordVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long recordId;

    /**
     * 分销员用户ID
     */
    @ExcelProperty(value = "分销员用户ID")
    private Long userId;

    /**
     * 被分销用户ID
     */
    @ExcelProperty(value = "被分销用户ID")
    private Long inviteeUserId;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private Long number;

    /**
     * 订单核销时间
     */
    @ExcelProperty(value = "订单核销时间")
    private Date orderUsedTime;

    /**
     * 奖励金额
     */
    @ExcelProperty(value = "奖励金额")
    private BigDecimal awardAmount;

    /**
     * 分销状态
     */
    @ExcelProperty(value = "分销状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "invitee_status")
    private String inviteeStatus;

    /**
     * 奖励状态
     */
    @ExcelProperty(value = "奖励状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "award_status")
    private String awardStatus;

    /**
     * 奖励时间
     */
    @ExcelProperty(value = "奖励时间")
    private Date awardTime;

    /**
     * 奖励发放账号
     */
    @ExcelProperty(value = "奖励发放账号")
    private String awardAccount;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;


}
