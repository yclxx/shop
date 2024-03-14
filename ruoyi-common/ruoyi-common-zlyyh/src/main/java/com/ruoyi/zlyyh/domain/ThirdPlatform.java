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
 * 第三方平台信息配置对象 t_third_platform
 *
 * @author yzg
 * @date 2024-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_third_platform")
public class ThirdPlatform extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * appId
     */
    private Long appId;
    /**
     * 密钥
     */
    private String secret;
    /**
     * 类型
     */
    private String type;
    /**
     * 平台名称
     */
    private String appName;
    /**
     * 状态：0-正常，1-禁用
     */
    private String status;

}
