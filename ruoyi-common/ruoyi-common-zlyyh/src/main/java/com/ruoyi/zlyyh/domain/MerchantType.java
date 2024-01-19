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
 * 商户门店类别对象 t_merchant_type
 *
 * @author yzg
 * @date 2024-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_merchant_type")
public class MerchantType extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 商户类别id
     */
    @TableId(value = "merchant_type_id")
    private Long merchantTypeId;
    /**
     * 类别名称
     */
    private String typeName;
    /**
     * 状态  0-正常  1-停用
     */
    private String status;
    /**
     * 删除标志  0-存在  2-删除
     */
    @TableLogic
    private String delFlag;

}
