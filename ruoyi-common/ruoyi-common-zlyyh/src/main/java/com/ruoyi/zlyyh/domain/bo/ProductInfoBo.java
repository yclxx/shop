package com.ruoyi.zlyyh.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品拓展业务对象
 *
 * @author yzg
 * @date 2023-05-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductInfoBo extends BaseEntity {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空", groups = { EditGroup.class })
    private Long productId;

    /**
     * 产品标题
     */
    @NotBlank(message = "产品标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 产品主图
     */
    @NotBlank(message = "产品主图不能为空", groups = { AddGroup.class, EditGroup.class })
    private String mainPicture;

    /**
     * 售卖起始时间（秒）
     */
    @NotBlank(message = "售卖起始时间（秒）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String saleStartTime;

    /**
     * 售卖结束时间（秒）
     */
    @NotBlank(message = "售卖结束时间（秒）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String saleEndTime;

    /**
     * 第三方产品id
     */
    @NotBlank(message = "第三方产品id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String itemId;

    /**
     * 第三方产品结算价格
     */
    //@NotBlank(message = "第三方产品id不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal itemPrice;

    /**
     * 折扣
     */
    @NotBlank(message = "折扣不能为空", groups = { AddGroup.class, EditGroup.class })
    private String discount;

    /**
     * 产品库存
     */
    @NotNull(message = "产品库存不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long stock;

    /**
     * 销量
     */
    private Long totalSales;

    /**
     * 适用门店数量（city_id不为空则返回当前城市可用门店数，否则返回全部可用门店数）
     */
    private Long applyShopCount;

    /**
     * 使用次数
     */
    @NotNull(message = "使用次数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long useTimes;

    /**
     * 佣金比例
     */
    @NotBlank(message = "佣金比例不能为空", groups = { AddGroup.class, EditGroup.class })
    private String commissionRate;

    /**
     * 活动价（分）
     */
    @NotNull(message = "活动价（分）不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal activityPriceCent;

    /**
     * 原价（分）
     */
    @NotNull(message = "原价（分）不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal originalPriceCent;

    /**
     * 套餐内容
     */
    @NotBlank(message = "套餐内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String itemContentGroup;

    /**
     * 套餐图片
     */
    @NotBlank(message = "套餐图片不能为空", groups = { AddGroup.class, EditGroup.class })
    private String itemContentImage;

    /**
     * 购买须知
     */
    @NotBlank(message = "购买须知不能为空", groups = { AddGroup.class, EditGroup.class })
    private String itemBuyNote;

    /**
     * 补充说明
     */
    @NotBlank(message = "补充说明不能为空", groups = { AddGroup.class, EditGroup.class })
    private String reserveDesc;

    /**
     * 商家须知
     */
    @NotBlank(message = "商家须知不能为空", groups = { AddGroup.class, EditGroup.class })
    private String shopInfo;

    /**
     * 使用须知
     */
    @NotBlank(message = "使用须知不能为空", groups = { AddGroup.class, EditGroup.class })
    private String useNote;

    /**
     * 使用时间
     */
    @NotBlank(message = "使用时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ticketTimeRule;

    /**
     * 人数限制
     */
    @NotNull(message = "人数限制不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userNumLimited;

    /**
     * 相对有效期，单位：天
     */
    private String period;

    /**
     * 限购数量 -1不限
     */
    @NotNull(message = "限购数量 -1不限不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long buyLimit;

    /**
     * 品牌
     */
    @NotBlank(message = "品牌不能为空", groups = { AddGroup.class, EditGroup.class })
    private String brandName;

    private Boolean shopAll;
    private Boolean overdue;
    private Boolean anyTime;
    private BigDecimal leastPrice;
    private BigDecimal reducePrice;
}
