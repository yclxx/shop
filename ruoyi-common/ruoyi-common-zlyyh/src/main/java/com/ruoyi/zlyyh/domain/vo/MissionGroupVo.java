package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;



/**
 * 任务组视图对象
 *
 * @author yzg
 * @date 2023-05-10
 */
@Data
@ExcelIgnoreUnannotated
public class MissionGroupVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long missionGroupId;

    /**
     * 任务组名称
     */
    @ExcelProperty(value = "任务组名称")
    private String missionGroupName;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

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
     * 规则图片
     */
    @ExcelProperty(value = "规则图片")
    private String missionImg;

    /**
     * 默认背景图片
     */
    private String missionBgImg;
    /**
     * 规则按钮图片
     */
    private String regulationButton;

    /**
     * 上传的背景图片
     */
    private String realBjImg;

    /**
     * 任务组编号
     */
    @ExcelProperty(value = "任务组编号")
    private String missionGroupUpid;

    /**
     * 积点兑红包：0-不可兑换，1-可兑换
     */
    @ExcelProperty(value = "积点兑红包", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "jd_convert_red_packet")
    private String jdConvertRedPacket;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 创建者
     */
    @ExcelProperty(value = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ExcelProperty(value = "更新者")
    private String updateBy;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    private String showCity;

    private BigDecimal convertValue;
}
