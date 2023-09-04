package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 门店商户号视图对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@ExcelIgnoreUnannotated
public class ShopMerchantVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 门店ID
     */
    @ExcelProperty(value = "门店ID")
    private Long shopId;

    /**
     * 商户号
     */
    @ExcelProperty(value = "商户号")
    private String merchantNo;

    /**
     * 商户类型（0-微信 1-云闪付 2-支付宝）
     */
    @ExcelProperty(value = "商户类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_shop_merchant_type")
    private String merchantType;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_shop_merchant_status")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
