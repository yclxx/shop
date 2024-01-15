package com.ruoyi.zlyyhadmin.domain.bo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 商户导入模板
 */
@Data
@ExcelIgnoreUnannotated
public class MerchantImportBo {
    private static final long serialVersionUID = 1L;
    /**
     * 商户名称
     */
    @ExcelProperty(value = "商户名称")
    private String merName;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String address;

    /**
     * 类别
     */
    @ExcelProperty(value = "类别")
    private String type;

    /**
     * 省份
     */
    @ExcelProperty(value = "市")
    private String province;
}
