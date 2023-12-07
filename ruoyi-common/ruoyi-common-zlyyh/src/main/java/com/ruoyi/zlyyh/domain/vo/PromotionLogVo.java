package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;



/**
 * 推广任务记录视图对象
 *
 * @author yzg
 * @date 2023-11-22
 */
@Data
@ExcelIgnoreUnannotated
public class PromotionLogVo {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 商户申请表id
     */
    @ExcelProperty(value = "商户申请表id")
    private Long approvalId;

    /**
     * 品牌名称
     */
    @ExcelProperty(value = "品牌名称")
    private String approvalBrandName;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long taskId;

    /**
     * 任务名称
     */
    @ExcelProperty(value = "任务名称")
    private String taskName;

    /**
     * 核销员id
     */
    @ExcelProperty(value = "核销员id")
    private Long verifierId;

    /**
     * 核销员手机号
     */
    @ExcelProperty(value = "核销员手机号")
    private String verifierMobile;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 拒绝原因
     */
    @ExcelProperty(value = "拒绝原因")
    private String reason;


}
