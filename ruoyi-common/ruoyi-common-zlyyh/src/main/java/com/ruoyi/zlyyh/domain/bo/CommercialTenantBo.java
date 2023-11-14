package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户业务对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CommercialTenantBo extends BaseEntity {

    /**
     * 商户ID
     */
    @NotNull(message = "商户ID不能为空", groups = {EditGroup.class})
    private Long commercialTenantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空", groups = {AddGroup.class, EditGroup.class})
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
     * 是否共享
     */
    private String is_share;
    /**
     * 供应商
     */
    private String supplier;
    /**
     * 营业执照
     */
    private String license;
    /**
     * 性质
     */
    private String nature;
    /**
     * 发票类型
     */
    private String invoice;
    /**
     * 收款账户
     */
    private String account;
    /**
     * 活动类型
     */
    private String activity;

    /**
     * 第三方品牌ID
     */
    private Long brandId;

    /**
     * 第三方品牌ID（银联）
     */
    private String ylBrandId;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 排序：从小到大
     */
    private Long sort;

    /**
     * 平台标识
     */
    //@NotNull(message = "平台标识不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long platformKey;

    private Long categoryId;

    private Long[] productIds;
    private BigDecimal latitude;

    private BigDecimal longitude;

    private Long[] categoryIds;

    private String weekDate;

    private Long shopId;
}
