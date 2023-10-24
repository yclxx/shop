package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户申请审批对象 t_merchant_approval
 *
 * @author yzg
 * @date 2023-10-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_merchant_approval")
public class MerchantApproval extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "approval_id")
    private Long approvalId;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 品牌logo
     */
    private String brandLogo;
    /**
     * 门店名称
     */
    private String shopName;
    /**
     * 门店电话
     */
    private String shopMobile;
    /**
     * 门店类型
     */
    private String shopType;
    /**
     * 门店地址
     */
    private String shopAddress;
    /**
     * 门店地址详情
     */
    private String shopAddressInfo;
    /**
     * 门店图片
     */
    private String shopImage;
    /**
     * 营业周
     */
    private String businessWeek;
    /**
     * 每天营业开始时间
     */
    private String businessBegin;
    /**
     * 每天营业结束时间
     */
    private String businessEnd;
    /**
     * 性质
     */
    private String nature;
    /**
     * 扩展服务商
     */
    private String extend;
    /**
     * 参与活动
     */
    private String activity;
    /**
     * 发票类型
     */
    private String invoiceType;
    /**
     * 收款账户
     */
    private String account;
    /**
     * 云闪付商户号
     */
    private String ysfMerchant;
    /**
     * 微信商户号
     */
    private String wxMerchant;
    /**
     * 支付宝商户号
     */
    private String payMerchant;
    /**
     * 商品类型
     */
    private String productType;
    /**
     * 审批状态
     */
    private String approvalStatus;
    /**
     * 拒绝原因
     */
    private String rejectReason;

}
