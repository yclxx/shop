package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.util.Date;


/**
 * 商品券码视图对象
 *
 * @author yzg
 * @date 2023-09-20
 */
@Data
@ExcelIgnoreUnannotated
public class CodeVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 商品ID
     */
    @ExcelProperty(value = "商品ID")
    private Long productId;

    /**
     * 场次ID
     */
    @ExcelProperty(value = "场次ID")
    private Long productSessionId;

    /**
     * 规格ID
     */
    @ExcelProperty(value = "规格ID")
    private Long productSkuId;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    private String productName;

    /**
     * 场次名称
     */
    @ExcelProperty(value = "场次名称")
    private String productSessionName;

    /**
     * 规格名称
     */
    @ExcelProperty(value = "规格名称")
    private String productSkuName;

    /**
     * 券号
     */
    @ExcelProperty(value = "券号")
    private String codeNo;

    /**
     * 分配状态：0-未分配，1-已分配
     */
    @ExcelProperty(value = "分配状态：0-未分配，1-已分配", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_code_allocation_state")
    private String allocationState;

    /**
     * 所属订单号
     */
    @ExcelProperty(value = "所属订单号")
    private Long number;

    /**
     * 核销状态：0-未核销，1-已核销，2-已失效，3-已作废
     */
    @ExcelProperty(value = "核销状态：0-未核销，1-已核销，2-已失效，3-已作废", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_code_used_status")
    private String usedStatus;

    /**
     * 券码类型：0-系统券码，1-外部券码
     */
    @ExcelProperty(value = "券码类型：0-系统券码，1-外部券码", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_code_type")
    private String codeType;

    /**
     * 核销或作废时间
     */
    @ExcelProperty(value = "核销或作废时间")
    private Date usedTime;

    /**
     * 核销店铺ID
     */
    @ExcelProperty(value = "核销店铺ID")
    private Long shopId;

    /**
     * 核销店铺名称
     */
    @ExcelProperty(value = "核销店铺名称")
    private String shopName;

    /**
     * 核销人员ID
     */
    @ExcelProperty(value = "核销人员ID")
    private Long verifierId;

    /**
     * 核销人员手机号
     */
    @ExcelProperty(value = "核销人员手机号")
    private String verifierMobile;

    /**
     * 二维码图片URL
     */
    @ExcelProperty(value = "二维码图片URL")
    private String qrcodeImgUrl;

    /**
     * 预约店铺ID
     */
    @ExcelProperty(value = "预约店铺ID")
    private Long appointmentShopId;

    /**
     * 预约店铺名称
     */
    @ExcelProperty(value = "预约店铺名称")
    private String appointmentShopName;

    /**
     * 预约时间
     */
    @ExcelProperty(value = "预约时间")
    private Date appointmentDate;

    /**
     * 预约状态：0-未预约，1-已预约，2-已取消
     */
    @ExcelProperty(value = "预约状态：0-未预约，1-已预约，2-已取消", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "appointment_status")
    private String appointmentStatus;

    /**
     * 预约时间ID
     */
    @ExcelProperty(value = "预约时间ID")
    private Long appointmentId;

    /**
     * 部门id
     */
    @ExcelProperty(value = "部门id")
    private Long sysDeptId;

    /**
     * 用户id
     */
    @ExcelProperty(value = "用户id")
    private Long sysUserId;

    /**
     * 核销数量
     */
    private Long usedCount;
    /**
     * 预约数量
     */
    private Long appointmentCount;
}
