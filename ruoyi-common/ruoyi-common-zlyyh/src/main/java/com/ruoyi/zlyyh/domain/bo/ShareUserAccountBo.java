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
 * 分销用户账户业务对象
 *
 * @author yzg
 * @date 2023-11-09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ShareUserAccountBo extends BaseEntity {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { EditGroup.class })
    private Long userId;

    /**
     * 是否可提现：0-可提现，1-禁止提现
     */
    private String status;

    /**
     * 冻结金额
     */
    private BigDecimal freezeBalance;

    /**
     * 可提金额
     */
    private BigDecimal balance;

    /**
     * 已提金额
     */
    private BigDecimal withdrawDeposit;

    /**
     * 已退金额
     */
    private BigDecimal refundBalance;


}
