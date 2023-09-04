package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商户号业务对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String merchantName;

    /**
     * 商户号
     */
    @NotBlank(message = "商户号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String merchantNo;

    /**
     * 证书地址
     */
    @NotBlank(message = "证书地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String certPath;

    /**
     * 证书密码
     */
    @NotBlank(message = "证书密码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String merchantKey;

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 支付成功回调通知地址
     */
    @NotBlank(message = "支付成功回调通知地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String payCallbackUrl;

    /**
     * 退款成功回调通知地址
     */
    @NotBlank(message = "退款成功回调通知地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String refundCallbackUrl;


}
