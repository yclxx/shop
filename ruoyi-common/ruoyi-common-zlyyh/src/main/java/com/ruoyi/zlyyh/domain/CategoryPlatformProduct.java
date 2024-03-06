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
 * 多平台栏目商品关联对象 t_category_platform_product
 *
 * @author yzg
 * @date 2024-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category_platform_product")
public class CategoryPlatformProduct extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 多平台栏目ID
     */
    private Long categoryPlatformId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 排序：从小到大
     */
    private Long sort;

}
