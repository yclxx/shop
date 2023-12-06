package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推广任务记录业务对象
 *
 * @author yzg
 * @date 2023-11-22
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PromotionLogBo extends BaseEntity {
    /**
     * 平台标识
     */
    private Long platformKey;
    /**
     * 品牌名称
     */
    private String approvalBrandName;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 核销员手机号
     */
    private String verifierMobile;
    /**
     * 状态
     */
    private String status;
}
