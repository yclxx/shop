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
 * 商品券包对象 t_product_package
 *
 * @author yzg
 * @date 2023-06-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product_package")
public class ProductPackage extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "package_id")
    private Long packageId;
    /**
     * 券包ID
     */
    private Long productId;
    /**
     * 包内产品
     */
    private Long extProductId;
    /**
     * 发放数量
     */
    private Long sendCount;
    /**
     * 状态
     */
    private String status;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
