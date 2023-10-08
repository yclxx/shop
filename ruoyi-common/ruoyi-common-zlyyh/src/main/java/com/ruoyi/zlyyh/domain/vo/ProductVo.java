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
 * 商品视图对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
@ExcelIgnoreUnannotated
public class ProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long productId;

    /**
     * 外部产品ID
     */
    @ExcelProperty(value = "外部产品ID")
    private String externalProductId;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    private String productName;

    /**
     * 简称
     */
    @ExcelProperty(value = "简称")
    private String productAbbreviation;

    /**
     * 副标题
     */
    @ExcelProperty(value = "副标题")
    private String productSubhead;

    /**
     * 商品图片
     */
    @ExcelProperty(value = "商品图片")
    private String productImg;

    /**
     * 商品归属：0-商城内部（自己开发的页面），1-宣传展示（外部提供的页面）
     */
    @ExcelProperty(value = "商品归属", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_product_affiliation")
    private String productAffiliation;

    /**
     * 商品类型：0-优惠券，2-优惠活动
     */
    @ExcelProperty(value = "商品类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_product_type")
    private String productType;

    /**
     * 领取方式：0-免费领取，1-付费领取，2-积点兑换
     */
    @ExcelProperty(value = "领取方式", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_product_pickup_method")
    private String pickupMethod;

    /**
     * 是否显示市场价格：0-不显示，1-显示
     */
    @ExcelProperty(value = "是否显示市场价格", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_product_show_original_amount")
    private String showOriginalAmount;

    /**
     * 市场价格
     */
    @ExcelProperty(value = "市场价格")
    private BigDecimal originalAmount;

    /**
     * 售卖价格
     */
    @ExcelProperty(value = "售卖价格")
    private BigDecimal sellAmount;

    /**
     * 62会员价格
     */
    @ExcelProperty(value = "62会员价格")
    private BigDecimal vipUpAmount;

    /**
     * 权益会员价格
     */
    @ExcelProperty(value = "权益会员价格")
    private BigDecimal vipAmount;

    /**
     * 跳转类型：0-无需跳转，1-内部页面，2-外部页面，3-小程序跳转，4-图片页面，5-RN跳转
     */
    @ExcelProperty(value = "跳转类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_product_to_type")
    private String toType;

    /**
     * 小程序ID
     */
    @ExcelProperty(value = "小程序ID")
    private String appId;

    /**
     * 页面地址
     */
    @ExcelProperty(value = "页面地址")
    private String url;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_product_status")
    private String status;

    /**
     * 展示开始时间
     */
    @ExcelProperty(value = "展示开始时间")
    private Date showStartDate;

    /**
     * 展示结束时间
     */
    @ExcelProperty(value = "展示结束时间")
    private Date showEndDate;

    /**
     * 领取开始时间
     */
    @ExcelProperty(value = "领取开始时间")
    private Date sellStartDate;

    /**
     * 领取结束时间
     */
    @ExcelProperty(value = "领取结束时间")
    private Date sellEndDate;

    /**
     * 指定周几: 0-不指定，1-指定周几
     */
    @ExcelProperty(value = "指定周几", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "t_product_assign_date")
    private String assignDate;

    /**
     * 周几能领：1-周日，2-周一，3-周二...7-周六，多个之间用英文逗号隔开
     */
    @ExcelProperty(value = "周几能领")
    private String weekDate;

    /**
     * 几点开始领取,几点结束,格式：HH:mm:ss-HH:mm:ss
     */
    @ExcelProperty(value = "几点开始领取,几点结束")
    private String sellTime;

    /**
     * 总数量，-1为不限制抢购数量
     */
    @ExcelProperty(value = "总数量")
    private Long totalCount;

    /**
     * 每月数量，-1为不限制抢购数量
     */
    @ExcelProperty(value = "每月数量")
    private Long monthCount;

    /**
     * 每周数量，-1为不限制抢购数量
     */
    @ExcelProperty(value = "每周数量")
    private Long weekCount;

    /**
     * 每日数量，-1为不限制抢购数量
     */
    @ExcelProperty(value = "每日数量")
    private Long dayCount;

    /**
     * 每日同一用户限领数量，0为不限制
     */
    @ExcelProperty(value = "每日同一用户限领数量")
    private Long dayUserCount;

    /**
     * 每周同一用户限领数量，0为不限制
     */
    @ExcelProperty(value = "每周同一用户限领数量")
    private Long weekUserCount;

    /**
     * 当月同一用户限领数量，0为不限制
     */
    @ExcelProperty(value = "当月同一用户限领数量")
    private Long monthUserCount;

    /**
     * 活动周期同一用户限领数量，0为不限制
     */
    @ExcelProperty(value = "活动周期同一用户限领数量")
    private Long totalUserCount;

    /**
     * 商品详情
     */
    @ExcelProperty(value = "商品详情")
    private String description;

    /**
     * 活动提供方logo
     */
    @ExcelProperty(value = "活动提供方logo")
    private String providerLogo;

    /**
     * 活动提供方名称
     */
    @ExcelProperty(value = "活动提供方名称")
    private String providerName;

    /**
     * 标签,英文逗号隔开
     */
    @ExcelProperty(value = "标签")
    private String tags;

    /**
     * 展示城市：ALL-全部、否则城市行政区号，多个之间用英文逗号隔开
     */
    @ExcelProperty(value = "展示城市")
    private String showCity;

    /**
     * 支付商户ID
     */
    @ExcelProperty(value = "支付商户ID")
    private Long merchantId;

    /**
     * 门店组ID
     */
    @ExcelProperty(value = "门店组ID")
    private Long shopGroupId;
    /**
     * 门店ID
     */
    @ExcelProperty(value = "门店ID")
    private String shopId;

    /**
     * 按钮名称
     */
    @ExcelProperty(value = "按钮名称")
    private String btnText;

    /**
     * 分享标题
     */
    @ExcelProperty(value = "分享标题")
    private String shareTitle;

    /**
     * 分享描述
     */
    @ExcelProperty(value = "分享描述")
    private String shareName;

    /**
     * 分享图片
     */
    @ExcelProperty(value = "分享图片")
    private String shareImage;

    /**
     * 平台标识
     */
    @ExcelProperty(value = "平台标识")
    private Long platformKey;

    /**
     * 排序：从小到大
     */
    @ExcelProperty(value = "排序")
    private Long sort;

    private String categoryId;

    private String commercialTenantId;


    private ProductInfoVo productInfoVo;

    /**
     * 发放金额
     */
    private BigDecimal externalProductSendValue;

    /**
     * 是否可被搜索
     */
    private String search;
    /**
     * 是否能被搜索
     */
    private String searchStatus;

    /**
     * 可购买用户：0-所有用户，1-62VIP用户
     */
    private String payUser;

    /**
     * 是否显示在首页
     */
    private String showIndex;

    /**
     * 发券账号类型：0-手机号，1-openId
     */
    private String sendAccountType;

    private String cusRefund;

    /**
     * 部门id
     */
    private Long sysDeptId;

    /**
     * 用户id
     */
    private Long sysUserId;

    /**
     * 银联分销：0-不通过，1-通过
     */
    private String unionPay;

    /**
     * 银联产品编号
     */
    private String unionProductId;

    /**
     * 可使用开始时间
     */
    private Date usedStartTime;

    /**
     * 可使用结束时间
     */
    private Date usedEndTime;

    /**
     * 是否校验用户购买城市
     */
    private String checkPayCity;

    private ProductTicketVo ticket;

    private List<ProductTicketSessionVo> ticketSession;

    private ShopVo shopVo;
}
