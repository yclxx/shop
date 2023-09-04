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
 * 商户号对象 t_merchant
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_merchant")
public class Merchant extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 商户名称
     */
    private String merchantName;
    /**
     * 商户号
     */
    private String merchantNo;
    /**
     * 证书地址
     */
    private String certPath;
    /**
     * 证书密码
     */
    private String merchantKey;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 支付成功回调通知地址
     */
    private String payCallbackUrl;
    /**
     * 退款成功回调通知地址
     */
    private String refundCallbackUrl;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
