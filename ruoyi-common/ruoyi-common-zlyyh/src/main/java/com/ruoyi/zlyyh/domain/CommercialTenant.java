package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户对象 t_commercial_tenant
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_commercial_tenant")
public class CommercialTenant extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 商户ID
     */
    @TableId(value = "commercial_tenant_id")
    private Long commercialTenantId;
    /**
     * 商户名称
     */
    private String commercialTenantName;
    /**
     * 商户logo
     */
    private String commercialTenantImg;
    /**
     * 标签,英文逗号隔开
     */
    private String tags;
    /**
     * 生效时间
     */
    private Date startTime;
    /**
     * 失效时间
     */
    private Date endTime;
    /**
     * 是否显示在首页（0显示 1不显示）
     */
    private String indexShow;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 第三方品牌ID
     */
    private Long brandId;
    /**
     * 第三方品牌ID（银联）
     */
    private String ylBrandId;
    /**
     * 排序：从小到大
     */
    private Long sort;
    /**
     * 平台标识
     */
    private Long platformKey;

    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;
}
