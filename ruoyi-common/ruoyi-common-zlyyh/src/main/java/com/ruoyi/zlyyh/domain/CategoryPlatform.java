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
 * 多平台类别对象 t_category_platform
 *
 * @author yzg
 * @date 2024-02-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category_platform")
public class CategoryPlatform extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 类别名称
     */
    private String name;
    /**
     * 类别ID，对应category表，多个之间用逗号隔开
     */
    private String categoryIds;

}
