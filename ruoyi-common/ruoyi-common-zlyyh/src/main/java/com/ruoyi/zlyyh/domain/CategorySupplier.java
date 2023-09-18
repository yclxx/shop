package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商产品分类对象 t_category_supplier
 *
 * @author yzg
 * @date 2023-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category_supplier")
public class CategorySupplier extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 供应商编号（字典键值）
     */
    private String supplierId;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 供应商多级分类
     */
    private String fullName;
    /**
     * 分类Id(多个分类名称以英文逗号隔开)
     */
    private String categoryId;

}
