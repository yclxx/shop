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
 * 退款订单登记业务对象
 *
 * @author yzg
 * @date 2023-08-07
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class RefundBo extends BaseEntity {

    /**
     * 退款订单ID
     */
    @NotNull(message = "退款订单ID不能为空", groups = { EditGroup.class })
    private Long refundId;

    /**
     * 订单号
     */
    @NotNull(message = "订单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long number;

    /**
     * 退款金额
     */
    @NotNull(message = "退款金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal refundAmount;

    /**
     * 退款申请人
     */
    @NotBlank(message = "退款申请人不能为空", groups = { AddGroup.class, EditGroup.class })
    private String refundApplicant;

    /**
     * 退款审核人
     */
    private String refundReviewer;

    /**
     * 0=审核中，1=审核通过，2=审核不通过
     */
    @NotNull(message = "0=审核中，1=审核通过，2=审核不通过不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 审核拒绝原因
     */
    private String refuseReason;

    /**
     * 退款原因
     */
    private String refundRemark;


}
