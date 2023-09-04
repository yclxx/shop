package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 商户号视图对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@ExcelIgnoreUnannotated
public class MerchantVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 商户名称
     */
    @ExcelProperty(value = "商户名称")
    private String merchantName;

    /**
     * 商户号
     */
    @ExcelProperty(value = "商户号")
    private String merchantNo;

    /**
     * 证书地址
     */
    @ExcelProperty(value = "证书地址")
    private String certPath;

    /**
     * 证书密码
     */
    @ExcelProperty(value = "证书密码")
    private String merchantKey;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_merchant_status")
    private String status;

    /**
     * 支付成功回调通知地址
     */
    @ExcelProperty(value = "支付成功回调通知地址")
    private String payCallbackUrl;

    /**
     * 退款成功回调通知地址
     */
    @ExcelProperty(value = "退款成功回调通知地址")
    private String refundCallbackUrl;


}
