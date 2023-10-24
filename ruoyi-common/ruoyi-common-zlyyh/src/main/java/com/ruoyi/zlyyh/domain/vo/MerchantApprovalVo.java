package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

/**
 * 商户申请审批视图对象
 *
 * @author yzg
 * @date 2023-10-19
 */
@Data
@ExcelIgnoreUnannotated
public class MerchantApprovalVo {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long approvalId;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 品牌名称
     */
    @ExcelProperty(value = "品牌名称")
    private String brandName;

    /**
     * 品牌logo
     */
    @ExcelProperty(value = "品牌logo")
    private String brandLogo;

    /**
     * 门店名称
     */
    @ExcelProperty(value = "门店名称")
    private String shopName;

    /**
     * 门店电话
     */
    @ExcelProperty(value = "门店电话")
    private String shopMobile;

    /**
     * 门店类型
     */
    @ExcelProperty(value = "门店类型")
    private String shopType;

    /**
     * 门店地址
     */
    @ExcelProperty(value = "门店地址")
    private String shopAddress;
    /**
     * 门店地址详情
     */
    @ExcelProperty(value = "门店地址详情")
    private String shopAddressInfo;

    /**
     * 门店图片
     */
    @ExcelProperty(value = "门店图片")
    private String shopImage;

    /**
     * 营业周
     */
    @ExcelProperty(value = "营业周")
    private String businessWeek;

    /**
     * 每天营业开始时间
     */
    @ExcelProperty(value = "每天营业开始时间")
    private String businessBegin;

    /**
     * 每天营业结束时间
     */
    @ExcelProperty(value = "每天营业结束时间")
    private String businessEnd;

    /**
     * 性质
     */
    @ExcelProperty(value = "性质")
    private String nature;

    /**
     * 扩展服务商
     */
    @ExcelProperty(value = "扩展服务商")
    private String extend;

    /**
     * 参与活动
     */
    @ExcelProperty(value = "参与活动")
    private String activity;

    /**
     * 发票类型
     */
    @ExcelProperty(value = "发票类型")
    private String invoiceType;

    /**
     * 收款账户
     */
    @ExcelProperty(value = "收款账户")
    private String account;

    /**
     * 云闪付商户号
     */
    @ExcelProperty(value = "云闪付商户号")
    private String ysfMerchant;

    /**
     * 微信商户号
     */
    @ExcelProperty(value = "微信商户号")
    private String wxMerchant;

    /**
     * 支付宝商户号
     */
    @ExcelProperty(value = "支付宝商户号")
    private String payMerchant;

    /**
     * 商品类型
     */
    @ExcelProperty(value = "商品类型")
    private String productType;

    /**
     * 审批状态
     */
    @ExcelProperty(value = "审批状态", converter = ExcelDictConvert.class)
    //@ExcelDictFormat(dictType = "sys_normal_disable")
    private String approvalStatus;
    /**
     * 拒绝原因
     */
    private String rejectReason;
}
