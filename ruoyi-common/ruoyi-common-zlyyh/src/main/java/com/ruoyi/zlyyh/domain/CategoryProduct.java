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
 * 栏目商品关联对象 t_category_product
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category_product")
public class CategoryProduct extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 栏目ID
     */
    private Long categoryId;
    /**
     * 商品ID或商户ID，具体根据栏目内容类型决定
     */
    private Long productId;
    /**
     * 排序：从小到大
     */
    private Long sort;

}
