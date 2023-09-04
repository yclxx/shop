package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.util.List;

/**
 * 平台信息视图对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@ExcelIgnoreUnannotated
public class PlatformVo {

    private static final long serialVersionUID = 1L;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 平台名称
     */
    @ExcelProperty(value = "平台名称")
    private String platformName;

    /**
     * 小程序标题
     */
    @ExcelProperty(value = "小程序标题")
    private String platformTitle;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_platform_status")
    private String status;

    /**
     * appId
     */
    @ExcelProperty(value = "appId")
    private String appId;

    /**
     * 小程序ID
     */
    @ExcelProperty(value = "小程序ID")
    private String encryptAppId;

    /**
     * 密钥
     */
    @ExcelProperty(value = "密钥")
    private String secret;

    /**
     * 对称密钥
     */
    @ExcelProperty(value = "对称密钥")
    private String symmetricKey;

    /**
     * rsa签名私钥
     */
    @ExcelProperty(value = "rsa签名私钥")
    private String rsaPrivateKey;

    /**
     * rsa签名公钥
     */
    @ExcelProperty(value = "rsa签名公钥")
    private String rsaPublicKey;

    /**
     * 客服电话
     */
    @ExcelProperty(value = "客服电话")
    private String serviceTel;

    /**
     * 客服服务时间
     */
    @ExcelProperty(value = "客服服务时间")
    private String serviceTime;

    /**
     * 活动城市：ALL-全部、否则填城市行政区号，多个之间用英文逗号隔开
     */
    @ExcelProperty(value = "活动城市")
    private String platformCity;

    /**
     * 默认支付商户
     */
    @ExcelProperty(value = "默认支付商户")
    private Long merchantId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 活动城市
     */
    List<AreaVo> platformCityList;
}
