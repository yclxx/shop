package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品业务对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = {EditGroup.class})
    private Long productId;

    /**
     * 外部产品ID
     */
    private String externalProductId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String productName;

    /**
     * 简称
     */
    private String productAbbreviation;

    /**
     * 副标题
     */
    private String productSubhead;

    /**
     * 商品图片
     */
    private String productImg;

    /**
     * 商品归属：0-商城内部（自己开发的页面），1-宣传展示（外部提供的页面）
     */
    @NotBlank(message = "商品归属不能为空", groups = {AddGroup.class, EditGroup.class})
    private String productAffiliation;

    /**
     * 商品类型：0-优惠券，2-优惠活动
     */
    @NotBlank(message = "商品类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String productType;

    /**
     * 领取方式：0-免费领取，1-付费领取，2-积点兑换
     */
    @NotBlank(message = "领取方式不能为空", groups = {AddGroup.class, EditGroup.class})
    private String pickupMethod;

    /**
     * 是否显示市场价格：0-不显示，1-显示
     */
    private String showOriginalAmount;

    /**
     * 市场价格
     */
    private BigDecimal originalAmount;

    /**
     * 售卖价格
     */
    private BigDecimal sellAmount;

    /**
     * 62会员价格
     */
    private BigDecimal vipUpAmount;

    /**
     * 权益会员价格
     */
    private BigDecimal vipAmount;

    /**
     * 跳转类型：0-无需跳转，1-内部页面，2-外部页面，3-小程序跳转，4-图片页面，5-RN跳转
     */
    @NotBlank(message = "跳转类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String toType;

    /**
     * 小程序ID
     */
    private String appId;

    /**
     * 页面地址
     */
    private String url;

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private String status;

    /**
     * 展示开始时间
     */
    private Date showStartDate;

    /**
     * 展示结束时间
     */
    private Date showEndDate;

    /**
     * 领取开始时间
     */
    private Date sellStartDate;

    /**
     * 领取结束时间
     */
    private Date sellEndDate;

    /**
     * 指定周几: 0-不指定，1-指定周几
     */
    private String assignDate;

    /**
     * 周几能领：1-周日，2-周一，3-周二...7-周六，多个之间用英文逗号隔开
     */
    private String weekDate;

    /**
     * 几点开始领取,几点结束,格式：HH:mm:ss-HH:mm:ss
     */
    private String sellTime;

    /**
     * 总数量，-1为不限制抢购数量
     */
    private Long totalCount;

    /**
     * 每月数量，-1为不限制抢购数量
     */
    private Long monthCount;

    /**
     * 每周数量，-1为不限制抢购数量
     */
    private Long weekCount;

    /**
     * 每日数量，-1为不限制抢购数量
     */
    private Long dayCount;

    /**
     * 每日同一用户限领数量，0为不限制
     */
    private Long dayUserCount;

    /**
     * 每周同一用户限领数量，0为不限制
     */
    private Long weekUserCount;

    /**
     * 当月同一用户限领数量，0为不限制
     */
    private Long monthUserCount;

    /**
     * 活动周期同一用户限领数量，0为不限制
     */
    private Long totalUserCount;

    /**
     * 商品详情
     */
    private String description;

    /**
     * 活动提供方logo
     */
    private String providerLogo;

    /**
     * 活动提供方名称
     */
    private String providerName;

    /**
     * 标签,英文逗号隔开(已废弃，暂时保留)
     */
    private String tags;

    private List<Long> tagsList;

    /**
     * 展示城市：ALL-全部、否则城市行政区号，多个之间用英文逗号隔开
     */
    private String showCity;

    /**
     * 支付商户ID
     */
    private Long merchantId;

    /**
     * 门店组ID
     */
    private Long shopGroupId;

    /**
     * 门店ID
     */
    private String shopId;
    /**
     * 门店全选
     */
    private Boolean shopAll;

    /**
     * 按钮名称
     */
    private String btnText;

    /**
     * 分享标题
     */
    private String shareTitle;

    /**
     * 分享描述
     */
    private String shareName;

    /**
     * 分享图片
     */
    private String shareImage;

    /**
     * 平台标识
     */
//    @NotNull(message = "平台不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long platformKey;

    /**
     * 排序：从小到大
     */
    private Long sort;

    private String categoryId;

    private String categoryPlatformId;

    private String commercialTenantId;

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
    /**
     * 支持优惠券
     */
    private String isCoupon;
    /**
     * 是否分享
     */
    private String isShare;
    /**
     * 供应商
     */
    private String supplier;
    /**
     * 支持端
     */
    private String supportChannel;

    /**
     * 单次购买上限(购物车加号上限)
     */
    private Long lineUpperLimit;
    /**
     * 商品详情信息
     */
    private ProductInfoBo productInfo;
    /**
     * 演出票商品信息
     */
    private ProductTicketBo ticket;

    /**
     * 场次与票种
     */
    private List<ProductTicketSessionBo> ticketSession;

    private String payBankType;

    private String productSmallImg;
    private String productTypeImg;

    private String isPoup;

    private String poupText;

    private String couponTip;

    private String sharePermission;
    private String shareAmountType;
    private BigDecimal shareOneAmount;
    private BigDecimal shareTwoAmount;
    private String warnMessage;
    private String warnEmail;
    private Long warnCount;
    private String autoRefund;
    private Long productGroupId;

    /**
     * 机构账户代码
     */
    private String institutionAccountId;

    /**
     * 第三方机构产品编号
     */
    private String institutionProductId;
}
