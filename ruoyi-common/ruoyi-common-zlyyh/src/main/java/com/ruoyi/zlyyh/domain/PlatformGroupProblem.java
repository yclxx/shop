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
 * 用户入群问题反馈对象 t_platform_group_problem
 *
 * @author yzg
 * @date 2024-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_platform_group_problem")
public class PlatformGroupProblem extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 反馈内容
     */
    private String content;

}
