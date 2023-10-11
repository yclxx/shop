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
 * 标签对象 t_tags
 *
 * @author yzg
 * @date 2023-10-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_tags")
public class Tags extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "tags_id")
    private Long tagsId;
    /**
     * 名称
     */
    private String tagsName;
    /**
     * 类型
     */
    private String tagsType;

}
