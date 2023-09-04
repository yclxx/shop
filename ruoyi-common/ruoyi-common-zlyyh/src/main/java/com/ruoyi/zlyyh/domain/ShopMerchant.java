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
 * 门店商户号对象 t_shop_merchant
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop_merchant")
public class ShopMerchant extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 门店ID
     */
    private Long shopId;
    /**
     * 商户号
     */
    private String merchantNo;
    /**
     * 商户类型（0-微信 1-云闪付 2-支付宝）
     */
    private String merchantType;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
