package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.math.BigDecimal;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 历史订单取码记录业务对象
 *
 * @author yzg
 * @date 2023-08-01
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class HistoryOrderPushInfoBo extends BaseEntity {
    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 订单号
     */
    @NotNull(message = "订单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long number;

    /**
     * 取码(充值)订单号
     */
    @NotBlank(message = "取码(充值)订单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String pushNumber;

    /**
     * 供应商订单号
     */
    private String externalOrderNumber;

    /**
     * 取码提交供应商产品编号（供应商提供）如遇面值类的，填面值
     */
    private String externalProductId;

    /**
     * 订单状态 0-处理中 1-成功 2-失败
     */
    @NotBlank(message = "订单状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 交易失败原因
     */
    private String remark;

    /**
     * 发放金额
     */
    private BigDecimal externalProductSendValue;


}
