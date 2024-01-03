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
 * 品牌管理对象 t_brand
 *
 * @author yzg
 * @date 2023-12-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_brand")
public class Brand extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 品牌ID
     */
    @TableId(value = "brand_id")
    private Long brandId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 品牌logo
     */
    private String brandImg;
    /**
     * 状态
     */
    private String status;
    /**
     * 排序
     */
    private Long sort;

}
