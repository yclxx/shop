package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 商户视图对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@ExcelIgnoreUnannotated
public class CommercialTenantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户ID
     */
    @ExcelProperty(value = "商户ID")
    private Long commercialTenantId;

    /**
     * 商户名称
     */
    @ExcelProperty(value = "商户名称")
    private String commercialTenantName;

    /**
     * 商户logo
     */
    @ExcelProperty(value = "商户logo")
    private String commercialTenantImg;

    /**
     * 标签,英文逗号隔开
     */
    @ExcelProperty(value = "标签")
    private String tags;

    /**
     * 生效时间
     */
    @ExcelProperty(value = "生效时间")
    private Date startTime;

    /**
     * 失效时间
     */
    @ExcelProperty(value = "失效时间")
    private Date endTime;

    /**
     * 是否显示在首页（0显示 1不显示）
     */
    @ExcelProperty(value = "是否显示在首页", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_commercial_tenant_index_show")
    private String indexShow;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_commercial_tenant_status")
    private String status;

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

    /**
     * 第三方品牌ID
     */
    private Long brandId;

    /**
     * 第三方品牌ID（银联）
     */
    private String ylBrandId;

    private List<ProductVo> productCouponList;

    private List<ProductVo> productActivityList;

    private List<ProductVo> productFoodList;

    private ShopVo shopVo;

    private Long[] productIds;

    private Long[] categoryIds;

    /** 距离 千米 */
    private BigDecimal distance;
}
