package com.ruoyi.zlyyh.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.annotation.Sensitive;
import com.ruoyi.common.core.enums.SensitiveStrategy;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 门店视图对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@ExcelIgnoreUnannotated
public class ShopVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long shopId;

    /**
     * 商户ID
     */
    @ExcelProperty(value = "商户ID")
    private Long commercialTenantId;

    /**
     * 门店名称
     */
    @ExcelProperty(value = "门店名称")
    private String shopName;

    /**
     * 门店电话
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    @ExcelProperty(value = "门店电话")
    private String shopTel;

    /**
     * 营业时间
     */
    @ExcelProperty(value = "营业时间")
    private String businessHours;

    /**
     * 门店地址
     */
    @ExcelProperty(value = "门店地址")
    private String address;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_shop_status")
    private String status;

    /**
     * 结构化地址信息省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码
     */
    @ExcelProperty(value = "结构化地址信息")
    private String formattedAddress;

    /**
     * 省份名
     */
    @ExcelProperty(value = "省份名")
    private String province;

    /**
     * 城市名
     */
    @ExcelProperty(value = "城市名")
    private String city;

    /**
     * 地址所在区
     */
    @ExcelProperty(value = "地址所在区")
    private String district;

    /**
     * 省份编码（行政区号例如：浙江330000）
     */
    @ExcelProperty(value = "省份编码")
    private String procode;

    /**
     * 城市编码（行政区号例如：杭州330100）
     */
    @ExcelProperty(value = "城市编码")
    private String citycode;

    /**
     * 区域编码（行政区号例如：拱墅区330105）
     */
    @ExcelProperty(value = "区域编码")
    private String adcode;

    /**
     * 经度,基于高德地图
     */
    @ExcelProperty(value = "经度")
    private BigDecimal longitude;

    /**
     * 纬度,基于高德地图
     */
    @ExcelProperty(value = "纬度")
    private BigDecimal latitude;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 距离 千米
     */
    private BigDecimal distance;

}
