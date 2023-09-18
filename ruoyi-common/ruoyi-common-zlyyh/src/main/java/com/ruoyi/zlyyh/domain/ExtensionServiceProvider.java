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
 * 商户拓展服务商对象 t_extension_service_provider
 *
 * @author yzg
 * @date 2023-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_extension_service_provider")
public class ExtensionServiceProvider extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 服务商名称
     */
    private String providerName;
    /**
     * 服务商联系人
     */
    private String providerUserName;
    /**
     * 联系电话
     */
    private String providerUserMobile;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 部门id
     */
    private Long sysDeptId;
    /**
     * 用户id
     */
    private Long sysUserId;

}
