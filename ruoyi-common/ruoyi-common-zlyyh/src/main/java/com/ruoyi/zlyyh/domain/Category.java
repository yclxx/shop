package com.ruoyi.zlyyh.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.TreeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 栏目对象 t_category
 *
 * @author yzgnet
 * @date 2023-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category")
public class Category extends TreeEntity<Category> {

    private static final long serialVersionUID=1L;

    /**
     * 栏目ID
     */
    @TableId(value = "category_id")
    private Long categoryId;
    /**
     * 栏目名称
     */
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
    private Long platformKey;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

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
}
