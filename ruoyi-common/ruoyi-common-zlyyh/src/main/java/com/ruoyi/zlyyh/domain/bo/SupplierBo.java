package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商业务对象
 *
 * @author yzg
 * @date 2023-10-11
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long supplierId;

    /**
     * 供应商名称
     */
    @NotBlank(message = "供应商名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String supplierName;

    /**
     * 供应商地址
     */
    private String supplierAddress;

    /**
     * 对公账户
     */
    private String corporateAccount;

    /**
     * 联系人
     */
    private String linkman;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 签约日期
     */
    private Date contractDate;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 是否预警
     */
    private String warning;

    /**
     * 预警金额
     */
    private BigDecimal warningAmount;

    /**
     * 状态
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 预警邮箱
     */
    private String warningEmail;
}
