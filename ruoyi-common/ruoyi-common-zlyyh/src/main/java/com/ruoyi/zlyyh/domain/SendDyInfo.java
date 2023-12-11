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
 * 用户订阅对象 t_send_dy_info
 *
 * @author yzg
 * @date 2023-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_send_dy_info")
public class SendDyInfo extends BaseEntity {

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
     * 用户ID
     */
    private Long userId;
    /**
     * 用户openId
     */
    private String openId;
    /**
     * 订阅模板ID
     */
    private String tmplId;
    /**
     * 状态
     */
    private String status;
    /**
     * 次数
     */
    private Long dyCount;

}
