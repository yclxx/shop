package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 巡检商户号临时对象 t_shop_tour_ls_merchant
 *
 * @author yzg
 * @date 2024-03-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop_tour_ls_merchant")
public class ShopTourLsMerchant extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 巡检商户号临时表ID
     */
    @TableId(value = "tour_merchant_ls_id")
    private Long tourMerchantLsId;
    /**
     * 巡检ID
     */
    private Long tourId;
    /**
     * 巡检记录ID
     */
    private Long tourLogId;
    /**
     * 巡检人员ID
     */
    private Long verifierId;
    /**
     * 门店ID
     */
    private Long shopId;
    /**
     * 商户号
     */
    private String merchantNo;
    /**
     * 商户号类型  0-微信  1-云闪付  2-支付宝
     */
    private String merchantType;
    /**
     * 收款方式  1-住扫  2被扫
     */
    private String paymentMethod;
    /**
     * 收单机构
     */
    private String acquirer;
    /**
     * 终端编号
     */
    private String terminalNo;
    /**
     * 商编截图
     */
    private String merchantImg;
    /**
     * 是否邮储商编  0-是  1-否
     */
    private String ycMerchant;
    /**
     * 是否修改  0-是  1-不是
     */
    private String isUpdate;
    /**
     * 删除标志  0-存在  2-删除
     */
    @TableLogic
    private String delFlag;

    private String oldMerchantNo;

}
