package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.util.Date;

/**
 * 推广任务视图对象
 *
 * @author yzg
 * @date 2023-11-16
 */
@Data
@ExcelIgnoreUnannotated
public class PromotionTaskVo {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long taskId;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 任务名称
     */
    @ExcelProperty(value = "任务名称")
    private String taskName;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private Date startDate;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private Date endDate;

    /**
     * 活动性质
     */
    @ExcelProperty(value = "活动性质", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "activity_nature")
    private String activityNature;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 规则图片
     */
    @ExcelProperty(value = "规则图片")
    private String ruleImage;

    /**
     * 推广图片
     */
    @ExcelProperty(value = "推广图片")
    private String image;

    /**
     * 背景图片
     */
    @ExcelProperty(value = "背景图片")
    private String backgroundImage;

    /**
     * 展示城市
     */
    @ExcelProperty(value = "展示城市")
    private String showCity;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private String sort;
}
