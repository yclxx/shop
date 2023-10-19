package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 栏目业务对象
 *
 * @author yzgnet
 * @date 2023-03-31
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryBo extends TreeEntity<CategoryBo> {

    /**
     * 栏目ID
     */
    @NotNull(message = "栏目ID不能为空", groups = { EditGroup.class })
    private Long categoryId;

    /**
     * 栏目名称
     */
    @NotBlank(message = "栏目名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String categoryName;

    /**
     * 栏目内容类型：（0商品 1商户）
     */
    private String categoryListType;

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
    @NotNull(message = "平台标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long platformKey;

    private Long[] productIds;

    private Long[] commercialTenantIds;

    /**
     * 顶部图片
     */
    private String topImg;
    /**
     * 按钮颜色
     */
    private String btnColor;
    /**
     * 显示城市
     */
    private String showCity;
    /**
     * 指定周几：: 0-不指定，1-指定周几
     */
    private String assignDate;
    /**
     * 周几能领：1-周日，2-周一，3-周二...7-周六，多个之间用英文逗号隔开
     */
    private String weekDate;

    /**
     * 是否显示在首页
     */
    private String showIndex;
    /**
     * 支持端
     */
    private String supportChannel;
}
