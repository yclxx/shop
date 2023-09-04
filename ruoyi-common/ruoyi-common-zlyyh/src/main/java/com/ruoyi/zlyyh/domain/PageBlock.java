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
 * 版块模板对象 t_page_block
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_page_block")
public class PageBlock extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 版块模板名称
     */
    private String blockName;
    /**
     * 板块模版主键字段
     */
    private String mainField;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 排序，从小到大
     */
    private Long sort;
    /**
     * 备注
     */
    private String remark;

}
