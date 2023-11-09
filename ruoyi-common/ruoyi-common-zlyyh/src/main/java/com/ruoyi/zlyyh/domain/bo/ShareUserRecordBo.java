package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 分销记录业务对象
 *
 * @author yzg
 * @date 2023-11-09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ShareUserRecordBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long recordId;

    /**
     * 分销员用户ID
     */
    @NotNull(message = "分销员用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 被分销用户ID
     */
    @NotNull(message = "被分销用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long inviteeUserId;

    /**
     * 订单号
     */
    @NotNull(message = "订单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long number;

    /**
     * 订单核销时间
     */
    private Date orderUsedTime;

    /**
     * 奖励金额
     */
    private BigDecimal awardAmount;

    /**
     * 分销状态
     */
    private String inviteeStatus;

    /**
     * 奖励状态
     */
    private String awardStatus;

    /**
     * 奖励时间
     */
    private Date awardTime;

    /**
     * 奖励发放账号
     */
    private String awardAccount;


}
