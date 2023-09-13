package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;



/**
 * 栏目视图对象
 *
 * @author yzgnet
 * @date 2023-03-31
 */
@Data
@ExcelIgnoreUnannotated
public class CategoryVo {

    private static final long serialVersionUID = 1L;

    /**
     * 栏目ID
     */
    @ExcelProperty(value = "栏目ID")
    private Long categoryId;

    /**
     * 栏目名称
     */
    @ExcelProperty(value = "栏目名称")
    private String categoryName;

    /**
     * 栏目内容类型：（0商品 1商户）
     */
    @ExcelProperty(value = "栏目内容类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_category_list_type")
    private String categoryListType;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_category_status")
    private String status;

    /**
     * 父级id
     */
    @ExcelProperty(value = "父级id")
    private Long parentId;

    /**
     * 排序：从小到大
     */
    @ExcelProperty(value = "排序")
    private Long sort;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    private Long productIds[];

    private Long commercialTenantIds[];

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
